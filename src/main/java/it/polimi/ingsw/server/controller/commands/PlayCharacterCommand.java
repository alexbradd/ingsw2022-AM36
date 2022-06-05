package it.polimi.ingsw.server.controller.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.server.model.CharacterStep;
import it.polimi.ingsw.server.model.Phase;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Represents a "PLAY_CHARACTER" command. It invokes the effect of a {@link CharacterType} passing it some arguments
 * as {@link CharacterStep}
 */
public class PlayCharacterCommand extends SingleArgumentCommand<PlayCharacterCommand.CharacterInvocation> {
    /**
     * Creates a new PlayCharacterCommand from the specified JsonObject. The JsonObject must have the following
     * properties:
     *
     * <ul>
     *     <li>{@code type} must be "MOVE_STUDENTS"</li>
     *     <li>{@code username} must be a non-null string</li>
     *     <li>{@code gameId} must be a {@link Number}</li>
     *     <li>
     *         {@code arguments} must be an object with the following format:
     *
     *         <ul>
     *             <li>{@code character} must be a literal from {@link CharacterType}</li>
     *             <li>
     *                 {@code steps} must be an array of objects with the describing the various steps that the card
     *                 needs.
     * <p>
     *                 Each object will be essentially translated to a {@link CharacterStep} where the name of the
     *                 property will be the argument key and the value the argument value
     *             </li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param cmd the JsonObject from which to create the command
     * @throws IllegalArgumentException if any argument is null or {@code cmd} is not formatted correctly
     */
    public PlayCharacterCommand(JsonObject cmd) {
        this(cmd, UserCommandType.PLAY_CHARACTER);
    }

    /**
     * Try to create a new PlayCharacterCommand from the given JsonObject. The format of the JSON must be the same as
     * {@link #PlayCharacterCommand(JsonObject)}, however the {@code type} attribute must be equal to the specified
     * string.
     *
     * @param cmd  the JsonObject from which to create the new command
     * @param type the value that {@code cmd.type} must have
     * @throws IllegalArgumentException if any argument is null or if {@code cmd} is not formatted correctly
     */
    public PlayCharacterCommand(JsonObject cmd, UserCommandType type) {
        super(cmd, type, jsonElement -> {
            try {
                CharacterInvocation invocation = new Gson().fromJson(jsonElement, CharacterInvocation.class);
                if (invocation == null)
                    throw new IllegalArgumentException("object cannot be null");
                if (invocation.character == null || invocation.steps == null)
                    throw new IllegalArgumentException("object must have character and steps properties");
                if (invocation.steps.stream().anyMatch(Objects::isNull))
                    throw new IllegalArgumentException("object.steps must contains only non-null objects");
                return invocation;
            } catch (JsonSyntaxException e) {
                throw new IllegalArgumentException("object is not formatted correctly", e);
            }
        });
    }

    /**
     * Invokes a {@link CharacterType} effect with the specified arguments on behalf of the {@link Player} with the
     * given username.
     *
     * @param phase The {@link Phase} to update
     * @return a new {@link Phase} with the updates applied
     * @throws InvalidPhaseUpdateException        if the invoked Character's effect would lead to an illegal state
     * @throws InvalidPlayerException             if the player tied to this command does not have permission to execute
     *                                            the action
     * @throws InvalidCharacterParameterException if any of the {@link CharacterStep} passed contains illegal values
     * @throws UnsupportedOperationException      if the command is executed outside the correct phase of the game
     */
    @Override
    public Phase execute(Phase phase) throws InvalidPhaseUpdateException, InvalidPlayerException, InvalidCharacterParameterException {
        Player p = phase.authorizePlayer(getUsername());
        return phase.playCharacter(p, getArg().character, getArg().getStepsAsCharacterStep());
    }

    /**
     * Returns a human-readable string describing what modifications the command will do to the given phase.
     *
     * @return a human-readable string
     */
    @Override
    public String getModificationMessage() {
        return "Player " + getUsername() +
                " has activated the effect of the " + getArg().character +
                " character card";
    }

    /**
     * Bean that represents the object contained in the command arguments. To be created using Gson.fromJson().
     */
    static class CharacterInvocation {
        /**
         * The {@link CharacterType} to be invoked.
         */
        private CharacterType character = null;
        /**
         * A list of String-String maps that will be converted to an array of {@link CharacterStep}.
         */
        private List<HashMap<String, String>> steps = null;

        /**
         * Getter for the {@link CharacterType}
         *
         * @return the {@link CharacterType}
         */
        public CharacterType getCharacter() {
            return character;
        }

        /**
         * Setter for the {@link CharacterType}
         *
         * @param character the new {@link CharacterType}
         */
        public void setCharacter(CharacterType character) {
            this.character = character;
        }

        /**
         * Getter for the list of String-String maps
         *
         * @return the list of String-String maps
         */
        public List<HashMap<String, String>> getSteps() {
            return steps;
        }

        /**
         * Converts and returns the list of String-String maps into an array of {@link CharacterStep}
         *
         * @return an array of {@link CharacterStep}
         */
        public CharacterStep[] getStepsAsCharacterStep() {
            return this.steps.stream()
                    .map(map -> {
                        CharacterStep step = new CharacterStep();
                        map.forEach(step::setParameter);
                        return step;
                    })
                    .toArray(CharacterStep[]::new);
        }

        /**
         * Setter for the list of String-String maps
         *
         * @param steps the new list of String-String maps
         */
        public void setSteps(List<HashMap<String, String>> steps) {
            this.steps = steps;
        }
    }
}
