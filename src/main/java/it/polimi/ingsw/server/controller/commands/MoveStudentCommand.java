package it.polimi.ingsw.server.controller.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.Phase;
import it.polimi.ingsw.server.model.Player;
import it.polimi.ingsw.server.model.Student;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

/**
 * Represents a "MOVE_STUDENT" command. It moves a student from the entrance of a {@link Player} to either the
 * Hall or an island.
 */
public class MoveStudentCommand extends SingleArgumentCommand<MoveStudentCommand.MoveParameter> {
    /**
     * Creates a new MoveStudentCommand from the specified JsonObject. The JsonObject must have the following
     * properties:
     *
     * <ul>
     *     <li>{@code type} must be "MOVE_STUDENTS"</li>
     *     <li>{@code username} must be a non-null string</li>
     *     <li>{@code gameId} must be a {@link Number}</li>
     *     <li>
     *         {@code arguments} must be an array containing one object. This object will have a slightly different
     *         format depending on the type of movement:
     *
     *          <ul>
     *              <li>{@code destination} a literal from {@link MoveDestination}</li>
     *              <li>{@code color} a literal from {@link PieceColor}</li>
     *              <li>
     *                  {@code index} necessary only if {@code destination} is {@link MoveDestination#ISLAND}, a positive
     *                  number. Doubles will be floored to an integer.
     *              </li>
     *          </ul>
     *     </li>
     * </ul>
     *
     * @param cmd the JsonObject from which to create the command
     * @throws IllegalArgumentException if any argument is null or {@code cmd} is not formatted correctly
     */
    public MoveStudentCommand(JsonObject cmd) {
        this(cmd, UserCommandType.MOVE_STUDENT);
    }

    /**
     * Try to create a new MoveStudentCommand from the given JsonObject. The format of the JSON must be the same as
     * {@link #MoveStudentCommand(JsonObject)}, however the {@code type} attribute must be equal to the specified
     * string.
     *
     * @param cmd  the JsonObject from which to create the new command
     * @param type the value that {@code cmd.type} must have
     * @throws IllegalArgumentException if any argument is null or if {@code cmd} is not formatted correctly
     */
    public MoveStudentCommand(JsonObject cmd, UserCommandType type) {
        super(cmd, type, (jsonElement) -> {
            try {
                MoveParameter p = new Gson().fromJson(jsonElement, MoveParameter.class);
                if (p == null)
                    throw new IllegalArgumentException("object cannot be empty");
                if (p.destination == null || p.color == null)
                    throw new IllegalArgumentException("object must have destination and color properties");
                if (p.destination == MoveDestination.ISLAND && p.index < 0)
                    throw new IllegalArgumentException("object must have island index if destination is ISLAND");
                return p;
            } catch (JsonSyntaxException e) {
                throw new IllegalArgumentException("object is not formatted correctly", e);
            }
        });
    }

    /**
     * Moves a student of the specified color from the entrance of the player with the given username to either theirs
     * hall or to an island.
     *
     * @param phase The {@link Phase} to update
     * @return a new {@link Phase} with the updates applied
     * @throws InvalidPhaseUpdateException   if the hall of the player is full or the island with the given index
     *                                       doesn't exist
     * @throws InvalidPlayerException        if the player tied to this command does not have permission to execute the
     *                                       action
     * @throws UnsupportedOperationException if the command is executed outside the correct phase of the game.
     */
    @Override
    public Phase execute(Phase phase) throws InvalidPhaseUpdateException, InvalidPlayerException, InvalidCharacterParameterException {
        Player p = phase.authorizePlayer(getUsername());
        Tuple<? extends Phase, Student> t = phase.getFromEntrance(p, getArg().color);
        return switch (getArg().destination) {
            case HALL -> t.throwMap((modifiedPhase, s) -> modifiedPhase.addToHall(p, s))
                    .markStudentMove(p);
            case ISLAND -> t.throwMap((modifiedPhase, s) -> modifiedPhase.addToIsland(p, getArg().index, s))
                    .markStudentMove(p);
        };
    }

    /**
     * Returns a human-readable string describing what modifications the command will do to the given phase.
     *
     * @return a human-readable string
     */
    @Override
    public String getModificationMessage() {
        return "Player " + getUsername() +
                " has moved a Student of color " +
                getArg().color + " to " +
                switch (getArg().destination) {
                    case HALL -> "their hall";
                    case ISLAND -> "to island number " + getArg().index;
                };
    }

    /**
     * Bean that represents the object contained in the command arguments. To be created using Gson.fromJson().
     */
    static class MoveParameter {
        /**
         * The destination of the movement
         */
        private MoveDestination destination = null;
        /**
         * The color of the student to move
         */
        private PieceColor color = null;
        /**
         * Used only if {@link #destination} is {@link MoveDestination#ISLAND}, the index of the island where to move
         * the student
         */
        private int index = -1;

        /**
         * Setter for the instance's destination
         *
         * @param destination the new destination
         */
        public void setDestination(MoveDestination destination) {
            this.destination = destination;
        }

        /**
         * Setter for the instance's color
         *
         * @param color the new color
         */
        public void setColor(PieceColor color) {
            this.color = color;
        }

        /**
         * Setter for the instance's index.
         *
         * @param index the new index
         */
        public void setIndex(int index) {
            this.index = index;
        }

        /**
         * Getter for the instance's destination
         *
         * @return the destination
         */
        public MoveDestination getDestination() {
            return destination;
        }

        /**
         * Getter for the instance's color
         *
         * @return the color
         */
        public PieceColor getColor() {
            return color;
        }

        /**
         * Getter for the instance's index
         *
         * @return the index
         */
        public int getIndex() {
            return index;
        }
    }

    /**
     * Enum that defines constants for the various movement destinations. It can be:
     *
     * <ol>
     *     <li>{@code HALL} to indicate that the student should be moved to the player's hall</li>
     *     <li>{@code ISLAND} to indicate that the student should be moved to an island</li>
     * </ol>
     */
    enum MoveDestination {
        HALL, ISLAND
    }
}
