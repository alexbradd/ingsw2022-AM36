package it.polimi.ingsw.server.controller.commands;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.model.Assistant;
import it.polimi.ingsw.server.model.Phase;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import static it.polimi.ingsw.server.controller.Messages.asString;

/**
 * Represents a "CHOOSE_MAGE" command. It assigns a deck with a {@link Mage} the a {@link Player}.
 */
public class ChooseMageCommand extends SingleArgumentCommand<Mage> {

    /**
     * Creates a new ChooseMageCommand from the specified JsonObject. The JsonObject must have the following properties:
     *
     * <ul>
     *     <li>{@code type} must be "CHOOSE_MAGE"</li>
     *     <li>{@code username} must be a non-null string</li>
     *     <li>{@code gameId} must be a {@link Number}</li>
     *     <li>{@code arguments} must be an array of strings with values taken from {@link Mage} </li>
     * </ul>
     *
     * @param cmd the JsonObject from which to create the command
     * @throws IllegalArgumentException if any argument is null or {@code cmd} is not formatted correctly
     */
    public ChooseMageCommand(JsonObject cmd) {
        this(cmd, UserCommandType.CHOOSE_MAGE);
    }

    /**
     * Try to create a new ChooseMageCommand from the given JsonObject. The format of the JSON must be the same as
     * {@link ChooseMageCommand#ChooseMageCommand(JsonObject)}, however the {@code type} attribute must be equal to the
     * specified string.
     *
     * @param cmd  the JsonObject from which to create the new command
     * @param type the value that {@code cmd.type} must have
     * @throws IllegalArgumentException if any argument is null or if {@code cmd} is not formatted correctly
     */
    public ChooseMageCommand(JsonObject cmd, UserCommandType type) {
        super(cmd, type, jsonElement -> Mage.valueOf(asString(jsonElement)));
    }

    /**
     * Assigns a deck of {@link Assistant} cards with the specified {@link Mage} to the player with the given username.
     *
     * @param phase The {@link Phase} to update
     * @return a new {@link Phase} with the updates applied
     * @throws InvalidPhaseUpdateException   if the specified mage has already been chosen by another player
     * @throws InvalidPlayerException        if the player tied to this command does not have permission to execute the
     *                                       action
     * @throws UnsupportedOperationException if the command is executed outside the correct phase of the game.
     */
    @Override
    public Phase execute(Phase phase) throws InvalidPhaseUpdateException, InvalidPlayerException {
        Player p = phase.authorizePlayer(getUsername());
        return phase.chooseMageDeck(p, getArg());
    }

    /**
     * Returns a human-readable string describing what modifications the command will do to the given phase.
     *
     * @return a human-readable string
     */
    @Override
    public String getModificationMessage() {
        return "Player " + getUsername() + " has chosen " + getArg() + " as its mage";
    }
}
