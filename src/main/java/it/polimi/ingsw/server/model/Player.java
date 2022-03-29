package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;

import java.util.Objects;

/**
 * Stub class
 */
class Player {
    private final String username;

    private int towers;

    protected Player(String username) {
        this.username = username;
        towers = 8;
    }

    protected Tower sendTower() {
        towers--;
        return new Tower(TowerColor.BLACK, this);
    }

    protected void receiveTower(Tower tower) {
        towers++;
    }

    public int getNumOfTowers() {
        return towers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return username.equals(player.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return username;
    }
}
