package it.polimi.ingsw.enums;

/**
 * Enum containing the various property names used in th eUPDATE message
 */
public enum DiffKeys {
    PLAYER_LIST("playerList"),
    PROFESSORS("professors"),
    BOARDS("boards"),
    ISLAND_LIST("islandList"),
    ISLANDS("islands"),
    MOTHER_NATURE("motherNature"),
    HAS_PLAYED_CHARACTER("usedCharacter"),
    CHARACTERS("characters"),
    IS_SACK_EMPTY("isSackEmpty"),
    CLOUDS("clouds"),
    PHASE("phase"),
    CURRENT_PLAYER("currentPlayer");

    /**
     * The property name
     */
    private final String key;

    /**
     * Creates a new enum with the given key as value
     *
     * @param key the key
     */
    DiffKeys(String key) {
        this.key = key;
    }

    /**
     * Returns the key corresponding to the enum constant
     *
     * @return the key corresponding to the enum constant
     */
    public String getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return key;
    }
}
