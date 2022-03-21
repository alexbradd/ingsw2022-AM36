package it.polimi.ingsw.server.model;

/**
 * Represents the Mother Nature (MN) piece. MN iterates cyclically on groups of a {@link IslandList} using a
 * {@link IslandListIterator}. Everytime MN arrives at an {@link Island}, it calculates the influence of players and
 * assigns the island to the player with the maximum. To modify the influence calculation and maximum extraction,
 * setters for the {@link InfluenceCalculator} and {@link MaxExtractor} are provided. Both calculators are reset
 * after each movement.
 */
class MotherNature {
    /**
     * The iterator on the game's {@link IslandList}.
     */
    private final IslandListIterator iterator;

    /**
     * The {@link Island} on which Mother Nature is currently on.
     */
    private Island current;

    /**
     * The {@link InfluenceCalculator} to use for influence calculation.
     */
    private InfluenceCalculator calculator;

    /**
     * The {@link MaxExtractor} to use for maximum extraction.
     */
    private MaxExtractor extractor;

    /**
     * Creates a new Mother Nature that will move on the given {@link IslandList}.
     *
     * @param list the {@link IslandList} to move on
     * @throws IllegalArgumentException if {@code list} is null
     */
    MotherNature(IslandList list) {
        if (list == null) throw new IllegalArgumentException("list shouldn't be null");
        this.iterator = list.randomGroupIterator();
        current = null;
        calculator = new StandardInfluenceCalculator();
        extractor = new EqualityExclusiveMaxExtractor();
    }

    /**
     * Getter for the {@link InfluenceCalculator} currently in use.
     *
     * @return the {@link InfluenceCalculator} currently in use
     */
    InfluenceCalculator getCalculator() {
        return calculator;
    }

    /**
     * Sets the given InfluenceCalculator for use in the next movement.
     *
     * @param calculator the InfluenceCalculator to use in the next movement
     * @throws IllegalArgumentException if {@code calculator} is null
     */
    void setCalculator(InfluenceCalculator calculator) {
        if (calculator == null) throw new IllegalArgumentException("calculator shouldn't be null");
        this.calculator = calculator;
    }

    /**
     * Sets the given MaxExtractor for use in the next movement.
     *
     * @param extractor the InfluenceCalculator to use in the next movement
     * @throws IllegalArgumentException if {@code extractor} is null
     */
    void setExtractor(MaxExtractor extractor) {
        if (extractor == null) throw new IllegalArgumentException("extractor shouldn't be null");
        this.extractor = extractor;
    }

    /**
     * Executes a movement of the given number of steps. The number of steps should be greater or equal than 1.
     *
     * @param steps the number steps to take
     * @throws IllegalArgumentException if {@code steps} is less than 1
     */
    void move(int steps) {
        if (steps < 1) throw new IllegalArgumentException("Mother nature moves at least one step");
        for (int i = 0; i < steps; i++) current = iterator.next();
        assignTower();
        if (current.isBlocked()) current.popBlock();
        calculator = new StandardInfluenceCalculator();
        extractor = new EqualityExclusiveMaxExtractor();
    }

    /**
     * Assigns the current island to the {@link Player} that has the highest influence.
     */
    private void assignTower() {
        assignTower(current);
    }

    /**
     * Assigns the given {@link Island} to the {@link Player} that has the highest influence.
     *
     * @param island {@link Island} calculate influence on
     * @throws IllegalArgumentException if {@code island} is null
     */
    void assignTower(Island island) {
        if (island == null) throw new IllegalArgumentException("island shouldn't be null");
        calculator.calculateInfluences(island).flatMap(extractor).ifPresent(island::conquer);
    }
}
