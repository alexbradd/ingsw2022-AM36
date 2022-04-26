package it.polimi.ingsw.server.model;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import java.util.Objects;

/**
 * Represents the game's {@code Player} entity.
 * The class contains a reference to his in-game username, from which it is identified, and a reference to its
 * {@link Board} instance.
 *
 * @author Mattia Busso, Leonardo Bianconi
 * @see Board
 */
public class Player implements Jsonable {
    /**
     * The username of the player.
     */
    private final String username;

    /**
     * The base constructor
     *
     * @param username the username of the player
     */
    Player(String username) {
        this.username = username;
    }

    /**
     * The username getter.
     *
     * @return the username of the player
     */
    public String getUsername() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(username, player.username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return username;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement toJson() {
        return new JsonPrimitive(getUsername());
    }
}
