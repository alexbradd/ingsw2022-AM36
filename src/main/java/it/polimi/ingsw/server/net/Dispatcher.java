package it.polimi.ingsw.server.net;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.controller.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Thread that handles IO with a client's {@link Socket}, spawned by {@link Server}. It provides basic IO operations for
 * receiving, sending {@link JsonObject}. It can also notify its users about disconnections via a callback.
 */
public class Dispatcher implements Runnable {
    /**
     * NOOP callback. More elegant than checking for null on call.
     */
    private final static Runnable NOOP_CB = () -> {};
    /**
     * The {@link Socket} associated to this Dispatcher.
     */
    private final Socket socket;
    /**
     * Callback called on client disconnection. By default, it doesn't do anything.
     */
    private Runnable onDisconnect;

    /**
     * The default callback for {@link #onReceive}. It routes the command to the {@link MatchRegistry} instance.
     */
    public final Consumer<JsonObject> onReceiveDefault =
            (o) -> MatchRegistry.getInstance().executeCommand(this, o);

    /**
     * Callback called after a message from a client arrives.
     */
    private Consumer<JsonObject> onReceive;

    /**
     * Creates a new Dispatcher object wrapping the given {@link Socket}.
     *
     * @param socket the {@link Socket} to use
     * @throws IllegalArgumentException if {@code socket} is null
     */
    public Dispatcher(Socket socket) {
        if (socket == null) throw new IllegalArgumentException("socket shouldn't be null");
        this.socket = socket;
        this.onDisconnect = NOOP_CB;
        this.onReceive = onReceiveDefault;
    }

    /**
     * Dispatcher's infinite execution loop. It will read from the socket parsing any JSON it can and pipe it to the
     * message handler until the socket is disconnected.
     * <p>
     * If the socket disconnects, the onDisconnect callback is called and then the thread ends.
     * If the read object is not a valid {@link JsonObject}, an error message is sent back (see
     * {@link Messages#buildErrorMessage(String)}).
     */
    @Override
    public void run() {
        try (Socket s = socket) {
            while (s.isConnected()) {
                Optional<JsonObject> obj = receive();
                if (obj.isPresent())
                    onReceive.accept(obj.get());
                else
                    send(Messages.buildErrorMessage("Malformed JSON"));
            }
        } catch (IOException e) {
            System.out.println("Error while doing IO to socket: " + e);
        } catch (ClientDisconnectedException e) {
            System.out.println("Client disconnected...");
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            System.out.println("Executing callback...");
            onDisconnect.run();
            System.out.println("Closing connection...");
        }
    }

    /**
     * Sets the onDisconnect callback to the provided {@link Runnable}. If null is passed, the default NOOP callback
     * will be assigned.
     *
     * @param callback the new callback to use
     */
    public void setOnDisconnect(Runnable callback) {
        onDisconnect = Objects.requireNonNullElse(callback, NOOP_CB);
    }

    /**
     * Sets the onReceive callback to the provided {@code BiConsumer}. If null is passed, the
     * default NOOP callback will be assigned.
     *
     * @param callback the new callback to use
     */
    public void setOnReceive(Consumer<JsonObject> callback) {
        onReceive = Objects.requireNonNullElse(callback, onReceiveDefault);
    }

    /**
     * Reads a JSON object from the Socket and parses it into a {@link JsonObject}. If everything goes as planned, the
     * parsed object is returned wrapped in an {@link Optional}, otherwise an empty one is returned. If the socket
     * disconnects while reading, {@link ClientDisconnectedException} is thrown. The method blocks until a full object
     * has been read. The separator used between objects is an empty line.
     *
     * @return an {@link Optional} wrapping the parsed {@link JsonObject}.
     * @throws ClientDisconnectedException if the Socket disconnects while reading
     */
    public Optional<JsonObject> receive() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            String read;
            StringBuilder msg = new StringBuilder();
            while ((read = in.readLine()) != null) {
                if (read.equals("")) {
                    try {
                        JsonObject obj = JsonParser.parseString(msg.toString()).getAsJsonObject();
                        return Optional.of(obj);
                    } catch (JsonSyntaxException | IllegalStateException e) {
                        return Optional.empty();
                    }
                } else
                    msg.append(read).append('\n');
            }
            throw new ClientDisconnectedException();
        } catch (IOException e) {
            System.out.println("Error while doing IO to socket: " + e);
        }
        return Optional.empty();
    }

    /**
     * Writes to the Socket the {@link JsonObject} passed.
     *
     * @param toWrite the object to write to the Socket
     * @throws IllegalArgumentException if {@code toWrite} is null
     */
    public void send(JsonObject toWrite) {
        if (toWrite == null) throw new IllegalArgumentException("toWrite shouldn't be null");
        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            writer.println(toWrite);
            writer.println("");
        } catch (IOException e) {
            System.out.println("Error while doing IO to socket: " + e);
        }
    }

    public void setPlayingState(Match match) {
        setOnReceive(new InMatchCallback(this));
        setOnDisconnect(new DisconnectCallback(match));
    }

    public void setIdleState() {
        setOnReceive(null);
        setOnDisconnect(null);
    }
}
