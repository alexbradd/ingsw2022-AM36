package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;

public class StudentMovePhase extends ActionPhase {
    private int numStudentsMoved;

    /**
     * The base constructor.
     * @param g the {@link Game} instance
     */
    protected StudentMovePhase(Game g) {super(g);}

    /**
     * This constructor creates the StudentMovePhase of a specific player and a specific order of next players to play.
     * @param g the {@link Game} instance
     * @param iterator the {@link PlayerListIterator} instance, corresponding to next players to play
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
