package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.CharacterType;

/**
 * STUB
 */
class BlockCard {
    private final CharacterType owner;
    BlockCard(CharacterType owner) {
        this.owner = owner;
    }
    public CharacterType getOwner() {
        return owner;
    }
}
