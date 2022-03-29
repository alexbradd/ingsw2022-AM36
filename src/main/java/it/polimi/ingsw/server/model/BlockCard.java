package it.polimi.ingsw.server.model;

/**
 * Stub class
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
