package it.polimi.ingsw.server.model;

import com.google.gson.JsonPrimitive;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import java.util.Objects;

/**
 * The IteratedPhase class represents a {@link Phase} that has a current player that is authorized to modify the state
 * of the game.
 *
 * @author Leonardo Bianconi, Alexandru Gabriel Bradatan
 */
abstract class IteratedPhase extends Phase {
    /**
     * The current Player's username of this IteratedPhase.
     */
    private Player current;

    /**
     * Creates a new IteratedPhase with the given {@link Player} as its current.
     *
     * @param previous the previous Phase that led to the creation of this one
     * @param current  the current {@link Player}
     * @throws IllegalArgumentException if any parameter is null
     */
    IteratedPhase(Phase previous, Player current) {
        super(previous);
        if (current == null) throw new IllegalArgumentException("current cannot be null");
        this.current = current;
    }

    /**
     * Creates a shallow copy of the given IteratedPhase
     *
     * @param old the IteratedPhase to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    IteratedPhase(IteratedPhase old) {
        super(old);
        this.current = old.current;
    }

    /**
     * Getter for this phase's current player.
     *
     * @return this phase's current player.
     */

    Player getCurrentPlayer() {
        return current;
    }

    void setCurrentPlayer(Player p) {
        this.current = p;
    }

    /**
     * {@inheritDoc}
     */

    public Player authorizePlayer(String username) throws InvalidPlayerException {
        if (username == null) throw new IllegalArgumentException("username cannot be null");
        if (!current.getUsername().equals(username)) throw new InvalidPlayerException();
        return getCurrentPlayer();
    }
}
