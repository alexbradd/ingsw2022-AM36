package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.CharacterType;

/**
 * Class for representing a Block card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class BlockCard {
    /**
     * The {@link CharacterType} of the Character that owns this card
     */
    private final CharacterType owner;

    /**
     * Creates a new card owned by the given {@link CharacterType}
     *
     * @param owner the {@link CharacterType} of the owner of the card
     * @throws IllegalArgumentException if {@code owner} is null
     */
    BlockCard(CharacterType owner) {
        if (owner == null) throw new IllegalArgumentException("owner shouldn't be null");
        this.owner = owner;
    }

    /**
     * Getter for this card's owner's {@link CharacterType}.
     *
     * @return card's owner's {@link CharacterType}.
     */
    public CharacterType getOwner() {
        return owner;
    }
}
