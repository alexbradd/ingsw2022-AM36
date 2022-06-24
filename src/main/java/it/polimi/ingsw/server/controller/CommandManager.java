package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.net.Dispatcher;

import java.util.List;
import java.util.stream.Collectors;

import static it.polimi.ingsw.server.controller.Messages.*;

/**
 * This class implements a task that, until termination, takes match-related commands from the {@link #match} command
 * queue and executes them on the related {@link Game} instance. It then returns the response (in broadcast or to the
 * sender {@link Dispatcher}).
 * This task is meant to be executed on a separate thread from the main thread (that handles the {@link MatchRegistry}),
 * and every {@code Match} instance should have one (and only one) {@code CommandManager} related to it.
 *
 * @author Leonardo Bianconi
 * @see Match
 * @see Game
 */
public class CommandManager implements Runnable {
    /**
     * A {@link CommandStrategy} to use during the standard phases of a {@link Match}.
     */
    private final static CommandStrategy DEFAULT_STRATEGY = new StandardCommandStrategy();
    /**
     * A {@link CommandStrategy} to use during the {@code rejoining} phase.
     */
    private final static CommandStrategy REJOIN_STRATEGY = new StandardCommandStrategy();
    /**
     * The corresponding {@link Match} instance.
     */
    private final Match match;

    /**
     * The strategy to use for managing the command.
     */
    private CommandStrategy strategy;

    /**
     * The default constructor.
     *
     * @param match the {@link Match} instance to be bound to
     */
    CommandManager(Match match) {
        this.match = match;
    }

    /**
     * Setter for {@link #strategy}.
     *
     * @param strategy the strategy to use
     */
    private void setStrategy(CommandStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Main operation, repeated until thread termination. Manages the commands in the commands queue. If the queue has
     * at least one element, it takes (blocking method synchronizing on the queue) the first element and executes the
     * {@link #manageCommand(Tuple)} method.
     */
    @Override
    public void run() {
        try {
            while (!match.hasEnded()) {
                manageCommand(match.getCommands().take());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for the management of a single command of the queue. It does the following:
     * <ul>
     *     <li>Chooses which strategy to use, checking if the {@code Match} is in {@code rejoining} state or not</li>
     *     <li>Calls the {@link #strategy}'s {@code manageCommand()} method</li>
     *     <li>Saves the {@code Game} state on disk</li>
     *     <li>If the game is now ended, sends a {@code END} message in broadcast</li>
     * </ul>
     *
     * @param command a Tuple representing an element of the queue of commands ({@link UserCommand}, {@link Dispatcher})
     * @see StandardCommandStrategy#manageCommand(Tuple, Match)
     * @see RejoiningCommandStrategy#manageCommand(Tuple, Match)
     */
    void manageCommand(Tuple<UserCommand, Dispatcher> command) {
        System.out.println("EXECUTING GAME COMMAND [ID: " + match.getId() + "]: " + command.getFirst().getModificationMessage());

        if (match.isRejoiningState())
            setStrategy(REJOIN_STRATEGY);
        else
            setStrategy(DEFAULT_STRATEGY);

        strategy.manageCommand(command, match);

        new Thread(() -> MatchRegistry.getInstance()
                .getPersistenceManager()
                .commit(match.getId(), match.getGame().getPhase()))
                .start();

        Game g = match.getGame();
        if (g.isEnded()) {
            sendWinMessage(g.getWinners());
            MatchRegistry.getInstance().terminate(match.getId());
        }
    }

    private void terminateIfEmpty() {
        if (match.getDispatchers().isEmpty())
            MatchRegistry.getInstance().terminate(match.getId());
    }

    /**
     * Helper method that sends the {@code END} message to all connected players, specifying that a winner has been
     * found and sending out the winner(s) list (see protocol docs).
     *
     * @param winners a list of players that have won the game
     */
    private void sendWinMessage(List<Player> winners) {
        List<String> winnersNames = winners.stream()
                .map(Player::getUsername)
                .collect(Collectors.toList());

        JsonObject message = buildEndMessage(match.getId(), "A winner has been found.", winnersNames);
        match.sendBroadcast(message);
    }
}


