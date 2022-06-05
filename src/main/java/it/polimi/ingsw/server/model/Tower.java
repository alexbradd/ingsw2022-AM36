package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.TowerColor;

/**
 * The class that models a tower. It has a color (an entry of the {@link TowerColor} enum) that distinguishes it from
 * other players' towers. It is an immutable class.
 *
 * @author Mattia Busso, Leonardo Bianconi
 * @see TowerColor
 * @see Player
 */
public final class Tower {
    /**
     * The color of the tower, representing the player to which it belongs.
     */
    private final TowerColor color;

    /**
     * The {@link Player} that owns the tower.
     */
    private final Player owner;

    /**
     * The tower constructor
     *
     * @param color the color of the tower ({@link TowerColor})
     */
    Tower(TowerColor color, Player owner) {
        this.color = color;
        this.owner = owner;
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
     * The tower's owner getter ({@link Player} object).
     *
     * @return the owner of the tower
     */
    Player getOwner() {
        return owner;
    }
}
