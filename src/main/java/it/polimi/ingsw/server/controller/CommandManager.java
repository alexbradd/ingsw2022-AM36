package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.controller.commands.UserCommandType;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.PhaseDiff;
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
     * The corresponding {@link Match} instance.
     */
    private final Match match;

    /**
     * The default constructor.
     *
     * @param match the {@link Match} instance to be bound to
     */
    CommandManager(Match match) {
        this.match = match;
    }

    /**
     * Getter for the {@code match}.
     *
     * @return the {@code Match} the manager is related to
     */
    Match getMatch() {
        return match;
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
     * Method for the management of a single command of the queue. It executes it on the {@link #match}'s {@link Game}
     * instance; then, it does the following:
     * <ul>
     *     <li>If an exception is thrown inside the model, return a {@code ERR} message to the sender's
     *          {@link Dispatcher}</li>
     *     <li>Else, return a {@code UPDATE} message in broadcast</li>
     * </ul>
     *
     * @param command a Tuple representing an element of the queue of commands ({@link UserCommand}, {@link Dispatcher})
     */
    void manageCommand(Tuple<UserCommand, Dispatcher> command) {
        System.out.println("EXECUTING GAME COMMAND (ID: " + match.getId() + "): " + command.getFirst().getModificationMessage());

        Dispatcher sender = command.getSecond();
        Game g = match.getGame();

        PhaseDiff diff;
        try {
            diff = g.executeUserCommand(command.getFirst());
        } catch (Exception exc) {
            sender.send(buildErrorMessage(match.getId(), exc.getMessage()));
            terminateIfEmpty();
            return;
        }
        diff.addAttribute("cause", new JsonPrimitive(command.getFirst().getModificationMessage()));

        UserCommandType type = command.getFirst().getType();
        String username = command.getFirst().getUsername();

        if (type.equals(UserCommandType.JOIN)) {
            addPlayer(sender, username);
        } else if (type.equals(UserCommandType.LEAVE)) {
            removePlayer(sender, username);
            terminateIfEmpty();
        }

        JsonObject update = buildUpdateMessage(diff.toJson().getAsJsonObject(), match.getId());
        match.sendBroadcast(update);

        if (g.isEnded())
            sendWinMessage(g.getWinners());
    }

    private void terminateIfEmpty() {
        if (match.getDispatchers().isEmpty())
            MatchRegistry.getInstance().terminate(match.getId());
    }

    /**
     * Helper method that adds a new Tuple<{@link Dispatcher}, {@code String}> to the {@link Match}, and performs all
     * the needed after-join operations.
     *
     * @param dispatcher the {@link Dispatcher} instance of the player
     * @param username   the player's username
     */
    private void addPlayer(Dispatcher dispatcher, String username) {
        try {
            match.addDispatcher(dispatcher, username);
        } catch (IllegalArgumentException e) {
            dispatcher.send(buildErrorMessage(match.getId(), e.getMessage()));
        }
        dispatcher.setPlayingState(match);
    }

    /**
     * Helper method that removes the corresponding Tuple<{@link Dispatcher}, {@code String}> from the {@link Match},
     * and performs all the needed after-leave operations.
     *
     * @param dispatcher the {@link Dispatcher} instance of the player
     * @param username   the player's username
     */
    private void removePlayer(Dispatcher dispatcher, String username) {
        try {
            match.removeDispatcher(dispatcher, username);
        } catch (IllegalArgumentException e) {
            dispatcher.send(buildErrorMessage(match.getId(), e.getMessage()));
        }
        dispatcher.send(buildLeftMessage(match.getId()));
        dispatcher.setIdleState();
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


