package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;


/**
 * This phase represents the movement of Mother Nature, performed by the current player, for a certain amount of steps.
 * It also includes the influence calculation for the island Mother Nature stepped on. It keeps track of whether MN has
 * already been moved or not. After all its operations, it moves on to a new {@link CloudPickPhase}.
 *
 * @author Leonardo Bianconi
 * @see ActionPhase
 * @see StudentMovePhase
 * @see CloudPickPhase
 */
public class MNMovePhase extends ActionPhase {
    private boolean mnMoved;


    /**
     * This constructor creates the ActionPhase of a specific player and a specific order of next players to play.
     *
     * @param g             the {@link Game} instance
     * @param iterator      the {@link PlayerListIterator} instance, corresponding to next players to play
     */
    protected MNMovePhase(Game g, PlayerListIterator iterator, Player player) {
        super(g);
        this.iterator = iterator;
        this.curPlayer = player;
        mnMoved = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase doPhase() {
        while (!mnMoved) {
            try {
                game.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (checkWin())
            return new EndgamePhase(game, getWinners());

        return new CloudPickPhase(game, iterator, curPlayer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveMN(String username, int steps) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, IllegalArgumentException {
        if (username == null)
            throw new NullPointerException("username must not be null");
        if (!username.equals(curPlayer.getUsername()))
            throw new InvalidPlayerException();
        //if (steps < 1)
        //    throw new IllegalArgumentException("steps cannot be less than 1");
        if (steps > curPlayer.getLastPlayedAssistant().getOrderValue()) {
            throw new IllegalArgumentException("steps cannot be more than assistant value");
        }

        game.getMotherNature().move(steps);
        synchronized (game) {
            mnMoved = true;
        }
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
     *
     * @return an array containing the winning player(s). It is empty if there isn't a winner yet.
     */
    public Player[] getWinners() {
        return null;
    }
}
