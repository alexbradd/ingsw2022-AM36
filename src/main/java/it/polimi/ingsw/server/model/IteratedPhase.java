package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import java.util.Objects;

/**
 * The IteratedPhase class represents a {@link Phase} that has a current player that is authorized to modify the state
 * of the game.
 *
 * @author Leonardo Bianconi, Alexandru Gabriel Bradatan
 */
abstract class IteratedPhase extends Phase {
    private Board board;
    /**
     * The current Player's username of this IteratedPhase. We save the username since the object can change.
     */
    private final String current;

    /**
     * Creates a new IteratedPhase with the given {@link Player} as its current.
     *
     * @param board   the {@link Board} that this IteratedPhase will have
     * @param current the current {@link Player}'s username
     * @throws IllegalArgumentException if {@code current} is null
     */
    IteratedPhase(Board board, String current) {
        if (board == null) throw new IllegalArgumentException("board cannot be null");
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
        if (old == null) throw new IllegalArgumentException("old cannot be null");
        this.board = old.board;
        this.current = old.current;
    }

    /**
     * Getter for this phase's current player.
     *
     * @return this phase's current player.
     */
    Player getCurrentPlayer() {
        return board.getPlayers().stream()
                .filter(p -> Objects.equals(p.getUsername(), current))
                .findAny()
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * {@inheritDoc}
     */
    public Player authorizePlayer(String username) throws InvalidPlayerException {
        if (username == null) throw new IllegalArgumentException("username cannot be null");
        if (!current.equals(username))
            throw new InvalidPlayerException();
        return getCurrentPlayer();
    }
}
