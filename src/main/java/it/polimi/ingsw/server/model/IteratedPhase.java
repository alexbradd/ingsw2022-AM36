package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

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
     * @param prev   the previous {@link Phase} of the game
     * @throws IllegalArgumentException if any parameter is null
     */
    IteratedPhase(Phase prev) throws IllegalArgumentException {
        super(prev.parameters);
    }

    /**
     * Creates a shallow copy of the given IteratedPhase
     *
     * @param old the IteratedPhase to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
     IteratedPhase(IteratedPhase old) throws IllegalArgumentException {
        super(checkPhaseNotNull(old).parameters);
        current = old.current;
    }


    IteratedPhase(Phase old, Player current) throws IllegalArgumentException {
        super(checkPhaseNotNull(old).parameters);
        this.current = current;
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

    /**
     * Helper method for controlling that the previous phase passed to the constructor is not null. It is a static method
     * because it needs to be called before calling super().
     *
     * @param p the {@link Phase} to check
     * @return {@code p} if {@code p != null}
     * @throws IllegalArgumentException if {@code p} is null
     */
    private static Phase checkPhaseNotNull(Phase p) throws IllegalArgumentException {
        if (p == null) throw new IllegalArgumentException("phase must not be null.");
        return p;
    }
}
