package it.polimi.ingsw.server.controller.commands;


import com.google.gson.JsonObject;
import it.polimi.ingsw.server.model.Phase;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import static it.polimi.ingsw.server.controller.Messages.asNumber;

/**
 * Represents a "MOVE_MN" command. It moves Mother Nature a certain number of steps on behalf of the {@link Player}.
 */
public class MoveMnCommand extends SingleArgumentCommand<Integer> {
    /**
     * Creates a new MoveMnCommand from the specified JsonObject. The JsonObject must have the following
     * properties:
     *
     * <ul>
     *     <li>{@code type} must be "MVOE_MN"</li>
     *     <li>{@code username} must be a non-null string</li>
     *     <li>{@code gameId} must be a {@link Number}</li>
     *     <li>{@code arguments} must be an array of strings with a positive integers</li>
     * </ul>
     *
     * @param cmd the JsonObject from which to create the command
     * @throws IllegalArgumentException if any argument is null or {@code cmd} is not formatted correctly
     */
    public MoveMnCommand(JsonObject cmd) {
        this(cmd, UserCommandType.MOVE_MN);
    }

    /**
     * Try to create a new MoveMnCommand from the given JsonObject. The format of the JSON must be the same as
     * {@link #MoveMnCommand(JsonObject)}, however the {@code type} attribute must be equal to the
     * specified string.
     *
     * @param cmd  the JsonObject from which to create the new command
     * @param type the value that {@code cmd.type} must have
     * @throws IllegalArgumentException if any argument is null or if {@code cmd} is not formatted correctly
     */
    public MoveMnCommand(JsonObject cmd, UserCommandType type) {
        super(cmd, type, jsonElement -> {
            long n = asNumber(jsonElement);
            if (n < 0)
                throw new IllegalArgumentException("integer should be positive");
            return (int) n;
        });
    }

    /**
     * Move Mother Nature a certain number of steps on behalf of the {@link Player} with the given username.
     *
     * @param phase The {@link Phase} to update
     * @return a new {@link Phase} with the updates applied
     * @throws InvalidPhaseUpdateException   if the number of steps is invalid
     * @throws InvalidPlayerException        if the player tied to this command does not have permission to execute the
     *                                       action
     * @throws UnsupportedOperationException if the command is executed outside the correct phase of the game.
     */
    @Override
    public Phase execute(Phase phase) throws InvalidPhaseUpdateException, InvalidPlayerException {
        Player p = phase.authorizePlayer(getUsername());
        return phase.moveMn(p, getArg());
    }

    /**
     * Returns a human-readable string describing what modifications the command will do to the given phase.
     *
     * @return a human-readable string
     */
    @Override
    public String getModificationMessage() {
        return "Player " + getUsername() + " has moved Mother Nature " + getArg() + " steps";
    }
}
