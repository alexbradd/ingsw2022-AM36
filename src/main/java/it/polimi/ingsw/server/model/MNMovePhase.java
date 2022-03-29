package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;

public class MNMovePhase extends ActionPhase {
    private boolean mnMoved;

    /**
     * This constructor creates the ActionPhase of a specific player and a specific order of next players to play.
     * @param g the {@link Game} instance
     * @param iterator the {@link PlayerListIterator} instance, corresponding to next players to play
     * @param currentPlayer the {@link Player} instance, corresponding to the current player
     */
    protected MNMovePhase(Game g, PlayerListIterator iterator, Player currentPlayer) {
        super(g);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase doPhase() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveMN(String username, int steps) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, IllegalArgumentException {
        super.moveMN(username, steps);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkWin() {
        return super.checkWin();
    }

    /**
     * It returns an array of {@link Player} instances corresponding to the winners of the game.
     * @return an array containing the winning player(s). It is empty if there isn't a winner yet.
     */
    public Player[] getWinners() {return null;}
}
