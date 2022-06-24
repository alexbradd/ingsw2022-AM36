package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.net.Dispatcher;

/**
 * This interface represents a strategy to be used for managing a game {@link UserCommand} on a {@link Match}.
 *
 * @author Leonardo Bianconi
 * @see CommandManager
 */
public interface CommandStrategy {
    /**
     * The strategy method.
     *
     * @param command the {@link UserCommand} to manage
     * @param match   the {@link Match} on which to apply the command
     */
    void manageCommand(Tuple<UserCommand, Dispatcher> command, Match match);
}
