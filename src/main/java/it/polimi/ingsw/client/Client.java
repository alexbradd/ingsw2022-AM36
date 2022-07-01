package it.polimi.ingsw.client;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.ProgramOptions;
import it.polimi.ingsw.client.control.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class handles Client's side communication.
 *
 * @author Mattia Busso
 */
public class Client {

    /**
     * Main entry point.
     */
    public static void exec() {
        final Controller controller = new Controller();
        final Timer timer = new Timer();

        controller.initUI(ProgramOptions.getMode());
        try (Socket socket = new Socket(ProgramOptions.getAddress(), ProgramOptions.getPort())) {
            System.out.println("Connection established.\n");
            socket.setSoTimeout(ProgramOptions.getClientSocketTimeout());
            try (BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 OutputStreamWriter socketOut = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)) {
                timer.scheduleAtFixedRate(
                        buildPeriodicConnectivityChecker(socketOut),
                        0,
                        ProgramOptions.getConnectivityCheckInterval());
                controller.setOnUserMessage(userMessage -> {
                    try {
                        writeObjectToStream(socketOut, userMessage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                controller.setOnEnd(() -> {
                    timer.cancel();
                    if (!socket.isClosed()) {
                        try {
                            socket.getInputStream().close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                controller.toMainMenu();
                readWhileOpen(controller, socketIn, socketOut);
            }
        } catch (IOException ignored) {
        }
        if (controller.toRun()) {
            controller.setOnEnd(timer::cancel);
            controller.toDisconnectState();
        }
    }

    /**
     * Reads the messages sent by the server through the socket while
     * the connection is open and the application is set to run.
     *
     * @param controller the application's controller
     * @param socketIn   the socket reader
     * @param socketOut  the socket writer
     * @throws IOException if an error occurs
     */
    private static void readWhileOpen(Controller controller, BufferedReader socketIn, OutputStreamWriter socketOut) throws IOException {
        final Gson gson = new Gson();
        String read;
        StringBuilder msg = new StringBuilder();
        while ((read = socketIn.readLine()) != null) {
            if (read.equals("")) {
                JsonObject message = gson.fromJson(msg.toString(), JsonObject.class);
                if (isPing(message))
                    writeObjectToStream(socketOut, buildPing(message.get("id")));
                else if (isNotHeartbeat(message))
                    controller.manageServerEvent(message);
                msg = new StringBuilder();
            } else {
                msg.append(read).append('\n');
            }
        }
    }

    /**
     * Helper that constructs a PONG message with the given ID
     *
     * @param id the {@code id} property of the PING message
     * @return a PONG response
     * @throws IllegalArgumentException if any parameter is null
     */
    private static JsonObject buildPing(JsonElement id) {
        if (id == null) throw new IllegalArgumentException("id should not be null");
        JsonObject pong = new JsonObject();
        pong.addProperty("type", "PONG");
        pong.add("gameId", id);
        return pong;
    }

    /**
     * Creates a new {@link TimerTask} that periodically writes a HEARTBEAT message to the server
     *
     * @param out the {@link OutputStreamWriter}
     * @return a {@link TimerTask}
     */
    private static TimerTask buildPeriodicConnectivityChecker(OutputStreamWriter out) {
        return new TimerTask() {
            @Override
            public void run() {
                try {
                    writeObjectToStream(out, buildHeartbeat());
                } catch (IOException ignored) {
                }
            }
        };
    }

    /**
     * Creates a new HEARTBEAT message
     *
     * @return a new JsonObject representing a HEARTBEAT message
     */
    private static JsonObject buildHeartbeat() {
        JsonObject o = new JsonObject();
        o.addProperty("type", "HEARTBEAT");
        return o;
    }

    /**
     * Helper that returns true if the given message is of type PING
     *
     * @param message the message
     * @return true if message is of type PING
     * @throws IllegalArgumentException if any parameter is null
     */
    private static boolean isPing(JsonObject message) {
        if (message == null) throw new IllegalArgumentException("message should not be null");
        return message.get("type").getAsString().equals("PING");
    }

    /**
     * Returns true if the given message is not a HEARTBERAT message
     *
     * @param message the message
     * @return true if the given message is not a HEARTBERAT message
     * @throws IllegalArgumentException if any parameter is null
     */
    private static boolean isNotHeartbeat(JsonObject message) {
        if (message == null) throw new IllegalArgumentException("message should not be null");
        return !message.get("type").getAsString().equals("HEARTBEAT");
    }

    /**
     * Writes the given object to the specified writer
     *
     * @param writer the writer to use
     * @param obj    the object to write
     * @throws IOException              if any IO errors happened
     * @throws IllegalArgumentException if any parameter is null
     */
    private static void writeObjectToStream(OutputStreamWriter writer, JsonObject obj) throws IOException {
        if (writer == null) throw new IllegalArgumentException("writer should not be null");
        if (obj == null) throw new IllegalArgumentException("obj should not be null");
        writer.write(obj + "\n\n");
        writer.flush();
    }
}