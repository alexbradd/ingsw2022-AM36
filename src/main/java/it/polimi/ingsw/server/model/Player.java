package it.polimi.ingsw.server.model;

import java.util.Objects;

/**
 * Stub class
 */
class Player {
    private final String username;

    protected Player(String username) {
        this.username = username;
    }

    void receiveTower(Tower tower) {

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
