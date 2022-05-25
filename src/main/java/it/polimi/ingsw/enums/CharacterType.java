package it.polimi.ingsw.enums;

import it.polimi.ingsw.functional.Tuple;

import java.util.Arrays;
import java.util.List;

/**
 * Enum containing each Character in the game and its initial cost.
 *
 * @author Alexandru Gabriel Bradatan
 */
public enum CharacterType {
    PRIEST(1, 1, 1, true, false, new Tuple<>("card", ParameterType.PIECE_COLOR), new Tuple<>("island", ParameterType.ISLAND_INDEX)),
    INNKEEPER(2, 0, 0, false, false),
    HERALD(3, 1, 1, false, false, new Tuple<>("island", ParameterType.ISLAND_INDEX)),
    MESSENGER(1, 0, 0, false, false),
    HERBALIST(2, 1, 1, false, true, new Tuple<>("island", ParameterType.ISLAND_INDEX)),
    CENTAUR(3, 0, 0, false, false),
    JESTER(1, 1, 3, true, false, new Tuple<>("card", ParameterType.PIECE_COLOR), new Tuple<>("entrance", ParameterType.PIECE_COLOR)),
    KNIGHT(2, 0, 0, false, false),
    WIZARD(3, 1, 1, false, false, new Tuple<>("color", ParameterType.PIECE_COLOR)),
    BARD(1, 1, 2, false, false, new Tuple<>("entrance", ParameterType.PIECE_COLOR), new Tuple<>("hall", ParameterType.PIECE_COLOR)),
    PRINCESS(2, 1, 1, true, false, new Tuple<>("card", ParameterType.PIECE_COLOR)),
    THIEF(3, 1, 1, false, false, new Tuple<>("color", ParameterType.PIECE_COLOR));

    /**
     * This Character's initial cost
     */
    private final int initialCost;
    /**
     * The minimum amount of steps this Character requires for invocations
     */
    private final int minSteps;
    /**
     * The maximum amount of steps this Character considered during invocations
     */
    private final int maxSteps;
    /**
     * True if the characters has some students on it
     */
    private final boolean hasStudents;
    /**
     * True if the characters has some blocks on it
     */
    private final boolean hasBlocks;
    /**
     * A {@link List} of {@link Tuple} containing the key and the type of the parameters that must be present in
     * each Character step
     *
     * @see ParameterType
     */
    private final List<Tuple<String, ParameterType>> stepParameters;

    /**
     * Creates a new CharacterType with the given cost
     *
     * @param initialCost    the initial cost of this Character
     * @param minSteps       the minimum amount of steps this character requires
     * @param maxSteps       the maximum amount of steps this character uses
     * @param hasStudents    if this character has any students on top of it
     * @param hasBlocks      if this character has any blocks on top of it
     * @param stepParameters a list of name-type tuple describing the format of each step
     */
    @SafeVarargs
    CharacterType(int initialCost, int minSteps, int maxSteps, boolean hasStudents, boolean hasBlocks, Tuple<String, ParameterType>... stepParameters) {
        this.initialCost = initialCost;
        this.minSteps = minSteps;
        this.maxSteps = maxSteps;
        this.hasStudents = hasStudents;
        this.hasBlocks = hasBlocks;
        this.stepParameters = Arrays.stream(stepParameters).toList();
    }

    /**
     * Getter for this Character's initial cost.
     *
     * @return this Character's initial cost.
     */
    public int getInitialCost() {
        return initialCost;
    }

    /**
     * Getter for the minimum amount of steps this Character requires for invocations
     *
     * @return the minimum amount of steps this Character requires for invocations
     */
    public int getMinSteps() {
        return minSteps;
    }

    /**
     * Getter for the maximum amount of steps this Character considered during invocations
     *
     * @return the maximum amount of steps this Character considered during invocations
     */
    public int getMaxSteps() {
        return maxSteps;
    }

    /**
     * Returns true if this type of chard has students placed on it.
     *
     * @return true if this type of chard has students placed on it
     */
    public boolean hasStudents() {
        return hasStudents;
    }

    /**
     * Returns true if this type of chard has blocks placed on it.
     *
     * @return true if this type of chard has blocks placed on it
     */
    public boolean hasBlocks() {
        return hasBlocks;
    }

    /**
     * Returns a {@link List} of {@link Tuple} containing the key and the type of the parameters that must be present in
     * each Character step
     *
     * @return a {@link List} of {@link Tuple}
     * @see ParameterType
     */
    public List<Tuple<String, ParameterType>> getStepParameters() {
        return stepParameters;
    }

    /**
     * Represents the possible types accepted as Character parameters
     */
    public enum ParameterType {
        PIECE_COLOR, ISLAND_INDEX
    }
}
