package it.polimi.ingsw.server.controller.commands;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.model.Phase;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

/**
 * Represents a {@code JOIN} command. It adds a new player to the list of players of this game.
 */
public class JoinCommand extends AbstractCommand {
    /**
     * Creates a new JoinCommand from the given JsonObject. The JsonObject must have the following properties:
     *
     * <ul>
     *     <li>{@code type} must be "JOIN"</li>
     *     <li>{@code username} must be a non-null string</li>
     *     <li>{@code gameId} must be a {@link Number}</li>
     * </ul>
     *
     * @param cmd the JsonObject from which to create the new command
     * @throws IllegalArgumentException if {@code cmd} is null or not formatted correctly
     */
    public JoinCommand(JsonObject cmd) {
        this(cmd, UserCommandType.JOIN);
    }

    /**
     * Try to create a new JoinCommand from the given JsonObject. The format of the JSON must be the same as
     * {@link JoinCommand#JoinCommand(JsonObject)}, however the {@code type} attribute must be equal to the specified
     * string.
     *
     * @param cmd  the JsonObject from which to create the new command
     * @param type the value that {@code cmd.type} must have
     * @throws IllegalArgumentException if any argument is null or if {@code cmd} is not formatted correctly
     */
    public JoinCommand(JsonObject cmd, UserCommandType type) {
        super(cmd, type);
    }

    /**
     * Adds a new player with the username specified by {@link JoinCommand (JsonObject)} to the game.
     *
     * @param phase The {@link Phase} to update
     * @return a new {@link Phase} with the update applied
     * @throws InvalidPhaseUpdateException   if a player with the same username already exists or if the maximum number
     *                                       of players allowed has been reached
     * @throws UnsupportedOperationException if the command is executed outside the "lobby" phase of the game.
     */
    @Override
    public Phase execute(Phase phase) throws InvalidPhaseUpdateException {
        return phase.addPlayer(getUsername());
    }

    /**
     * Returns a human-readable string describing what modifications the command will do to the given phase.
     *
     * @return a human-readable string
     */
    @Override
    public String getModificationMessage() {
        return "Player " + getUsername() + " has joined the game";
    }
}
