package it.polimi.ingsw.server.model;

/**
 * This class models the game's towers.
 *
 * @author Mattia Busso
 */
class Tower {

    /**
     * The tower's associated color.
     */
    private final TowerColor color;

    /**
     * Reference to the Player owner of the tower.
     */
    private final Player owner;

    /**
     * Tower constructor.
     *
     * @param color
     * @param owner
     * @throws IllegalArgumentException owner should not be null
     */
    Tower(TowerColor color, Player owner) throws IllegalArgumentException {

        if (owner == null) {
            throw new IllegalArgumentException("owner should not be null");
        }

        this.color = color;
        this.owner = owner;
    }

    /**
     * This method sends the tower back to its owner.
     */
    void returnToOwner() {
        owner.receiveTower(this);
    }

    /**
     * Tower color getter.
     *
     * @return color
     */
    TowerColor getColor() {
        return this.color;
    }

    /**
     * Tower owner getter.
     *
     * @return owner
     */
    Player getOwner() {
        return this.owner;
    }

}
