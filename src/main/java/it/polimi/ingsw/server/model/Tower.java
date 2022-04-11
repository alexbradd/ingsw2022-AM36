package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;

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
    private TowerColor color;

    /**
     * The tower constructor
     * <<<<<<< Updated upstream
     * <p>
     * =======
     * >>>>>>> Stashed changes
     *
     * @param color the color of the tower ({@link TowerColor})
     */
    Tower(TowerColor color) {
        this.color = color;
    }

    /**
     * Tower color getter.
     *
     * @return color
     */
    TowerColor getColor() {
        return this.color;
    }
}
