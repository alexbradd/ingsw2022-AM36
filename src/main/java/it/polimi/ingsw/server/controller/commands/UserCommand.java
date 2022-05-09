package it.polimi.ingsw.server.controller.commands;

import it.polimi.ingsw.server.model.Phase;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

/**
 * Represents a user action in a client application. They contain the state modification associated with a given command
 * type and its arguments.
 */
public interface UserCommand {
    /**
     * Executes this command on the given {@link Phase}. It returns a new {@link Phase} representing the final state
     * after command execution. If the updates done by this command would lead to an illegal configuration, an exception
     * will be thrown.
     *
     * @param phase The {@link Phase} to update
     * @return a new {@link Phase} object with the updates applied
     * @throws InvalidPlayerException             if the {@link Player} linked to this command does not have permission
     *                                            to execute the action this command represents
     * @throws InvalidPhaseUpdateException        if the update would lead to an illegal game state
     * @throws InvalidCharacterParameterException if the parameters passed to character card invocation are not
     *                                            formatted correctly
     * @throws UnsupportedOperationException      if the update uses operations unsupported in the particular state the
     *                                            game is in
     */
    Phase execute(Phase phase) throws InvalidPhaseUpdateException, InvalidPlayerException, InvalidCharacterParameterException;

    /**
     * Returns the {@link UserCommandType} associated with this instance
     *
     * @return the {@link UserCommandType} associated with this instance
     */
    UserCommandType getType();

    /**
     * Returns the id of the game this command is for.
     *
     * @return the id of the game this command is for.
     */
    long getGameId();

    /**
     * Returns the username associated with this command
     *
     * @return the username associated with this command
     */
    String getUsername();

    /**
     * Returns a human-readable string describing what modifications the command will do to the given phase.
     *
     * @return a human-readable string
     */
    String getModificationMessage();
}
