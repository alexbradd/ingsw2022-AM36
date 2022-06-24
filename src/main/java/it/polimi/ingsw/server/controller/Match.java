package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.controller.commands.UserCommandType;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.net.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class represents a single match operated by the server. It has a unique identifier, the corresponding model's
 * {@link Game} instance, a list of connected {@link Dispatcher}s and a queue of stored commands to be managed by a
 * separate {@code Thread}. Its operations mainly permit to modify these attributes.
 *
 * @author Leonardo Bianconi
 * @see MatchRegistry
 * @see CommandManager
 * @see Pinger
 */
public class Match {
    /**
     * The time interval, expressed in milliseconds, for the {@link Pinger} to wait for receiving PONG messages.
     */
    private final static long WAIT_PONG_TIME = 1000;
    /**
     * The time interval, expressed in milliseconds, for new {@link Pinger} instances to be created.
     */
    private final static long PING_RATE = 5000;
    /**
     * The unique identifier of the Match.
     */
    private final long id;
    /**
     * The corresponding model's {@link Game} instance, that keeps track of the model's state of this Match.
     */
    private final Game game;
    /**
     * A list containing all the {@link Dispatcher}s that are currently connected to the game, bounded to their in-game
     * username.
     */
    private final List<Tuple<Dispatcher, String>> dispatcherList;
    /**
     * A {@link BlockingQueue} containing all the {@link UserCommand}s to be executed (via a {@link CommandManager} on a
     * separate thread), paired with the {@link Dispatcher} that "requested" the execution of such command.
     */
    private final BlockingQueue<Tuple<UserCommand, Dispatcher>> commands;
    /**
     * The corresponding {@link Pinger} instance.
     */
    private Pinger pinger;
    /**
     * A thread that runs the {@link #runPinger()} method.
     */
    private final Thread pingThread;
    /**
     * A thread that runs the {@link CommandManager#run()} method.
     */
    private final Thread commandThread;
    /**
     * Whether this match has ended or not.
     */
    private volatile boolean ended;

    /**
     * The default constructor.
     *
     * @param id   the id of the Match
     * @param game the {@link Game} instance corresponding to the Match
     * @throws IllegalArgumentException  if the {@link Game} passed is null
     * @throws IndexOutOfBoundsException if the {@code id} is negative
     */
    Match(int id, Game game) {
        if (id < 0)
            throw new IndexOutOfBoundsException("id must be positive.");

        if (game == null)
            throw new IllegalArgumentException("game must not be null");

        this.id = id;
        this.game = game;
        this.commands = new LinkedBlockingQueue<>();
        this.dispatcherList = new ArrayList<>();
        this.ended = false;

        this.pingThread = new Thread(this::runPinger);
        this.commandThread = new Thread(new CommandManager(this));
        pingThread.start();
        commandThread.start();

        System.out.println("NEW MATCH CREATED [ID: " + id + "]");
    }

    /**
     * Getter for the id.
     *
     * @return the Match id
     */
    long getId() {
        return id;
    }

    /**
     * Getter for the {@link Game} instance.
     *
     * @return the {@link Game} instance
     */
    Game getGame() {
        return game;
    }

    /**
     * Getter for the list of connected {@link Dispatcher}s.
     *
     * @return the list of connected {@link Dispatcher}s
     */
    List<Dispatcher> getDispatchers() {
        return dispatcherList.stream()
                .map(Tuple::getFirst)
                .collect(Collectors.toList());
    }

    List<Tuple<Dispatcher, String>> getDispatchersAndNames() {
        return new ArrayList<>(dispatcherList);
    }

    /**
     * Getter for the queue of commands.
     *
     * @return the queue of commands
     */
    BlockingQueue<Tuple<UserCommand, Dispatcher>> getCommands() {
        return commands;
    }

    /**
     * Getter for the ended attribute.
     * @return if the match has ended or not
     */
    boolean hasEnded() {
        return ended;
    }

    /**
     * Sets the match to ended.
     */
    void setEnded() {
        ended = true;
    }

    /**
     * Adds a {@link Dispatcher} instance to the currently connected Dispatchers list. This means this Dispatcher is now
     * "connected" to this Match.
     *
     * @param dispatcher the {@link Dispatcher} instance to be added
     * @throws IllegalArgumentException if {@code dispatcher == null} or if dispatcher is already connected to this
     *                                  Match
     */
    synchronized void addDispatcher(Dispatcher dispatcher, String username) throws IllegalArgumentException {
        if (dispatcher == null) throw new IllegalArgumentException("dispatcher must not be null.");
        if (username == null) throw new IllegalArgumentException("username must not be null.");

        if (getDispatchers().contains(dispatcher))
            throw new IllegalArgumentException("This socket is already connected to this Match.");

        dispatcherList.add(new Tuple<>(dispatcher, username));
    }

    /**
     * Removes a {@link Dispatcher} instance to the currently connected Dispatchers list. This means this Dispatcher is
     * now "disconnected" to this Match.
     *
     * @param dispatcher the {@link Dispatcher} instance to be removed
     * @throws IllegalArgumentException if {@code dispatcher == null} or if dispatcher is not connected to this Match
     */
    synchronized void removeDispatcher(Dispatcher dispatcher, String username) throws IllegalArgumentException {
        if (dispatcher == null) throw new IllegalArgumentException("dispatcher must not be null.");
        if (username == null) throw new IllegalArgumentException("username must not be null");
        if (!isCorrectUsername(dispatcher, username))
            throw new IllegalArgumentException("This username isn't bound to this socket.");
        if (!getDispatchers().contains(dispatcher))
            throw new IllegalArgumentException("This socket is not connected to this Match.");

        dispatcherList.removeIf(tuple -> tuple.getFirst().equals(dispatcher));
        dispatcher.setOnReceive(dispatcher.onReceiveDefault);
    }

    /**
     * Helper method that checks whether the {@code dispatcher} and the {@code username} passed correspond to a entry
     * in the {@link #dispatcherList}. If not, then the client probably tried to send a message with another username.
     *
     * @param dispatcher the {@link Dispatcher} instance
     * @param username   a string representing the player's username
     * @return whether the {@code username} is actually bound to the {@code dispatcher}
     */
    private boolean isCorrectUsername(Dispatcher dispatcher, String username) {
        return dispatcherList.stream()
                .anyMatch(t -> t.getFirst().equals(dispatcher) && t.getSecond().equals(username));
    }

    /**
     * This method executes a match-related command. It takes a {@link UserCommand} and the {@link Dispatcher} that sent
     * this command, creates a {@link Tuple} containing the two and adds it to {@link #commands}. The two parameters
     * are assumed to be correct and non-null.
     *
     * @param command    the {@link UserCommand} to be executed
     * @param dispatcher the requesting {@link Dispatcher}
     */
    void executeUserCommand(UserCommand command, Dispatcher dispatcher) {
        if (!isCorrectUsername(dispatcher, command.getUsername()) && !command.getType().equals(UserCommandType.JOIN))
            throw new IllegalArgumentException("Wrong username.");

        try {
            commands.put(new Tuple<>(command, dispatcher));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * It sends the specified {@link JsonObject} message to all the dispatchers connected to the Match.
     *
     * @param message the message to send
     */
    void sendBroadcast(JsonObject message) {
        for (Dispatcher dispatcher : getDispatchers())
            dispatcher.send(message);
    }

    /**
     * Method that executes the {@link Pinger} task at a fixed rate.
     */
    synchronized private void runPinger() {
        pinger = new Pinger(this, WAIT_PONG_TIME);
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (ended) {
                    t.cancel();
                    return;
                }
                pinger = new Pinger(Match.this, WAIT_PONG_TIME);
                pinger.run();
            }
        }, 0, PING_RATE);
    }

    /**
     * Getter for the {@link Pinger} instance.
     *
     * @return the {@link Pinger} instance.
     */
    synchronized public Pinger getPinger() {
        return pinger;
    }

    /**
     * Returns a {@code JsonObject} representation of this Match.
     *
     * @return a {@code JsonObject} representation of this Match
     */
    JsonObject toJson() {
        JsonObject j = new JsonObject();
        j.addProperty("id", id);
        j.addProperty("nPlayers", game.getNPlayers());
        j.addProperty("expert", game.isExpertMode());
        j.addProperty("playersConnected", dispatcherList.size());
        return j;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "match " + id +
                ": game=" + game +
                ", dispatcherList=" + dispatcherList +
                ", commands=" + commands +
                '}';
    }
}
