package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.net.Dispatcher;

import static it.polimi.ingsw.server.controller.Messages.buildErrorMessage;
import static it.polimi.ingsw.server.controller.Messages.buildLeftMessage;

/**
 * This class contains some utility methods for common operations among the {@link CommandStrategy} implementations.
 *
 * @author Leonardo Bianconi
 * @see CommandStrategy
 * @see CommandManager
 * @see Match
 */
public class CommandUtilities {
    /**
     * Helper method that adds a new Tuple<{@link Dispatcher}, {@code String}> to a {@link Match}, and performs all
     * the needed after-join operations.
     *
     * @param dispatcher the {@link Dispatcher} instance of the player
     * @param username   the player's username
     * @throws IllegalArgumentException if the match is in rejoining state and there wasn't a connected player with such
     *                                  username or {@link Match#addDispatcher(Dispatcher, String)} goes wrong
     */
    static void addPlayer(Dispatcher dispatcher, String username, Match match) throws IllegalArgumentException {
        if (match.isRejoiningState() && !match.getMissingPlayers().contains(username))
            throw new IllegalArgumentException("A player with such username wasn't connected to this match.");

        match.addDispatcher(dispatcher, username);
        dispatcher.setPlayingState(match);
    }

    /**
     * Helper method that removes the corresponding Tuple<{@link Dispatcher}, {@code String}> from a {@link Match},
     * and performs all the needed after-leave operations.
     *
     * @param dispatcher the {@link Dispatcher} instance of the player
     * @param username   the player's username
     * @param match      the {@code Match} from which to remove the player
     */
    static void removePlayer(Dispatcher dispatcher, String username, Match match) {
        try {
            match.removeDispatcher(dispatcher, username);
        } catch (IllegalArgumentException e) {
            dispatcher.send(buildErrorMessage(match.getId(), e.getMessage()));
        }
        dispatcher.send(buildLeftMessage(match.getId()));
        dispatcher.setIdleState();
    }
}
