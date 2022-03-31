package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;

/**
 * Tower stub
 */
public class Tower {
    TowerColor color;
    Tower(TowerColor color, Player owner) {
        this.color = color;
    }

    TowerColor getColor() {
        return this.color;
    }
}
