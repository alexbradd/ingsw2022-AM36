package it.polimi.ingsw.server.controller.commands;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.model.Phase;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

/**
 * Represents a {@code LEAVE} command. It removes a player from the list of players of this game.
 */
public class LeaveCommand extends AbstractCommand {
    /**
     * Creates a new LeaveCommand from the given JsonObject. The JsonObject must have the following properties:
     *
     * <ul>
     *     <li>{@code type} must be "LEAVE"</li>
     *     <li>{@code username} must be a non-null string</li>
     *     <li>{@code gameId} must be a {@link Number}</li>
     * </ul>
     *
     * @param cmd the JsonObject from which to create the new command
     * @throws IllegalArgumentException if {@code cmd} is null or not formatted correctly
     */
    public LeaveCommand(JsonObject cmd) {
        this(cmd, UserCommandType.LEAVE);
    }

    /**
     * Try to create a new LeaveCommand from the given JsonObject. The format of the JSON must be the same as
     * {@link LeaveCommand#LeaveCommand(JsonObject)}, however the {@code type} attribute must be equal to the specified
     * string.
     *
     * @param cmd  the JsonObject from which to create the new command
     * @param type the value that {@code cmd.type} must have
     * @throws IllegalArgumentException if any argument is null or if {@code cmd} is not formatted correctly
     */
    public LeaveCommand(JsonObject cmd, UserCommandType type) {
        super(cmd, type);
    }

    /**
     * Remove the player with the username specified by {@link LeaveCommand(JsonObject)} from the game.
     *
     * @param phase The {@link Phase} to update
     * @return a new {@link Phase} with the update applied
     * @throws InvalidPhaseUpdateException   if a player with the given username doesn't exist or if the game would have
     *                                       less than one player
     * @throws UnsupportedOperationException if the command is executed outside the "lobby" phase of the game.
     */
    @Override
    public Phase execute(Phase phase) throws InvalidPhaseUpdateException {
        return phase.removePlayer(getUsername());
    }

    /**
     * Returns a human-readable string describing what modifications the command will do to the given phase.
     *
     * @return a human-readable string
     */
    @Override
    public String getModificationMessage() {
        return "Player " + getUsername() + " has left the game";
    }
}
