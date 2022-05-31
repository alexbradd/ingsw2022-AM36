package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.net.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
class Match {
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
     * A list containing all the {@link Dispatcher}s that are currently connected to the game.
     */
    private final List<Dispatcher> dispatcherList;
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
     * The default constructor.
     *
     * @param id   the id of the Match
     * @param game the {@link Game} instance corresponding to the Match
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

        new Thread(new CommandManager(this)).start();
        new Thread(this::runPinger).start();

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
     * Adds a {@link Dispatcher} instance to the currently connected Dispatchers list. This means this Dispatcher is now
     * "connected" to this Match.
     *
     * @param dispatcher the {@link Dispatcher} instance to be added
     * @throws IllegalArgumentException if {@code dispatcher == null} or if dispatcher is already connected to this
     *                                  Match
     */
    synchronized void addDispatcher(Dispatcher dispatcher) throws IllegalArgumentException {
        if (dispatcher == null) throw new IllegalArgumentException("dispatcher must not be null.");
        if (dispatcherList.contains(dispatcher))
            throw new IllegalArgumentException("This socket is already connected to this Match.");
        dispatcherList.add(dispatcher);
    }

    /**
     * Removes a {@link Dispatcher} instance to the currently connected Dispatchers list. This means this Dispatcher is
     * now "disconnected" to this Match.
     *
     * @param dispatcher the {@link Dispatcher} instance to be removed
     * @throws IllegalArgumentException if {@code dispatcher == null} or if dispatcher is not connected to this Match
     */
    synchronized void removeDispatcher(Dispatcher dispatcher) throws IllegalArgumentException {
        if (dispatcher == null) throw new IllegalArgumentException("dispatcher must not be null.");
        if (!dispatcherList.contains(dispatcher))
            throw new IllegalArgumentException("This socket is not connected to this Match.");
        dispatcherList.remove(dispatcher);

        dispatcher.setOnReceive(dispatcher.onReceiveDefault);
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
        try {
            commands.put(new Tuple<>(command, dispatcher));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that executes the {@link Pinger} task at a fixed rate.
     */
    synchronized private void runPinger() {
        pinger = new Pinger(this, WAIT_PONG_TIME);
        new Timer().scheduleAtFixedRate(pinger, 0, PING_RATE);
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

        return j;
    }

    @Override
    public String toString() {
        return "match " + id +
                ": game=" + game +
                ", dispatcherList=" + dispatcherList +
                ", commands=" + commands +
                '}';
    }
}
