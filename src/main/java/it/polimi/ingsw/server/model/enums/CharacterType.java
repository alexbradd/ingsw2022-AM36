package it.polimi.ingsw.server.model.enums;

/**
 * Enum containing each Character in the game and its initial cost.
 *
 * @author Alexandru Gabriel Bradatan
 */
public enum CharacterType {
    PRIEST(1),
    INNKEEPER(2),
    HERALD(3),
    MESSENGER(1),
    HERBALIST(2),
    CENTAUR(3),
    JESTER(1),
    KNIGHT(2),
    WIZARD(3),
    BARD(1),
    PRINCESS(2),
    THIEF(3);

    /**
     * This Character's initial cost
     */
    private final int initialCost;

    /**
     * Creates a new CharacterType with the given cost
     *
     * @param initialCost the initial cost of this Character
     */
    CharacterType(int initialCost) {
        this.initialCost = initialCost;
    }

    /**
     * Getter for this Character's initial cost.
     *
     * @return this Character's initial cost.
     */
    public int getInitialCost() {
        return initialCost;
    }

}
