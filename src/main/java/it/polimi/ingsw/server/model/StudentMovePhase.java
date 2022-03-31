package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;

/**
 * This phase represents the first action a player must perform at the beginning of any action phase: moving
 * a certain amount of students (specified by the game rules) from their Entrance to another location (Island or
 * their Hall). It keeps track of how many students have been moved and moves on to a new {@link MNMovePhase} if
 * the amount of movements needed is performed.
 *
 * @author Leonardo Bianconi
 * @see ActionPhase
 * @see MNMovePhase
 */

public class StudentMovePhase extends ActionPhase {
    /**
     * The number of students already moved by the player.
     */
    private int numStudentsMoved;

    /**
     * The base constructor.
     *
     * @param g the {@link Game} instance
     */
    protected StudentMovePhase(Game g) {
        super(g);
    }

    /**
     * This constructor creates the StudentMovePhase of a specific player and a specific order of next players to play.
     *
     * @param g             the {@link Game} instance
     * @param iterator      the {@link PlayerListIterator} instance, corresponding to next players to play
     * @param currentPlayer the {@link Player} instance, corresponding to the current player
     */
    protected StudentMovePhase(Game g, PlayerListIterator iterator, Player currentPlayer) {
        super(g);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entrance getPlayerEntrance(String username) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException {
        return super.getPlayerEntrance(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Hall getPlayerHall(String username) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException {
        return super.getPlayerHall(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase doPhase() {
        return null;
    }
}
