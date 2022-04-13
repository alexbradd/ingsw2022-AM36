package it.polimi.ingsw.server.model;

/**
 * STUB
 */
class BlockCard {
    private final Herbalist owner;

    BlockCard(Herbalist owner) {
        this.owner = owner;
    }

    public void returnToOwner() {
        owner.pushBlock(this);
    }
}
