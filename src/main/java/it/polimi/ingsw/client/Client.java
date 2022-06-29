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

/**
 * This class handles Client's side communication.
 *
 * @author Mattia Busso
 */
public class Client {

    /**
     * Main entry point.
     *
     * @param opts the given program options
     */
    public static void exec(ProgramOptions opts) {
        final Controller controller = new Controller();
        try (Socket socket = new Socket(opts.getAddress(), opts.getPort());
             BufferedReader socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             OutputStreamWriter socketOut = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)) {
            System.out.println("Connection established.\n");
            controller.setOnUserMessage(userMessage -> {
                try {
                    writeObjectToStream(socketOut, userMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            controller.setOnEnd(() -> {
                try {
                    socket.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            controller.initAndStartUI(opts.getMode());
            readWhileOpen(controller, socket, socketIn, socketOut);
        } catch (IOException e) {
            if(controller.toRun()) {
                controller.setOnEnd(null);
                controller.toDisconnectState();
            }
        }
    }

    /**
     * Reads the messages sent by the server through the socket while
     * the connection is open and the application is set to run.
     *
     * @param controller the application's controller
     * @param socket the connection socket
     * @param socketIn the socket reader
     * @param socketOut the socket writer
     * @throws IOException if an error occurs
     */
    private static void readWhileOpen(Controller controller, Socket socket, BufferedReader socketIn, OutputStreamWriter socketOut) throws IOException {
        final Gson gson = new Gson();
        String read;
        while (controller.toRun() && !socket.isClosed()) {
            StringBuilder msg = new StringBuilder();
            while ((read = socketIn.readLine()) != null) {
                if (read.equals("")) {
                    JsonObject message = gson.fromJson(msg.toString(), JsonObject.class);
                    if (isPing(message))
                        writeObjectToStream(socketOut, buildPing(message.get("id")));
                    else
                        controller.manageServerEvent(message);
                    msg = new StringBuilder();
                } else {
                    msg.append(read).append('\n');
                }
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
     * Helper that returns true if the given message is of type PING
     *
     * @param message the message
     * @return ture if message is of type PING
     * @throws IllegalArgumentException if any parameter is null
     */
    private static boolean isPing(JsonObject message) {
        if (message == null) throw new IllegalArgumentException("message should not be null");
        return message.get("type").getAsString().equals("PING");
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