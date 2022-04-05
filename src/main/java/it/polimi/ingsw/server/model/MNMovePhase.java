package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.List;


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
     * @param g        the {@link Game} instance
     * @param iterator the {@link PlayerListIterator} instance, corresponding to next players to play
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
        return (lessThanThreeIslands() || playerHasNoTowers());
    }

    /**
     * Helper method that calculates the number of islands on the table and checks whether it less than or equal to 3.
     *
     * @return whether there are 3 or less islands
     */
    private boolean lessThanThreeIslands() {
        int nIslands = game.getIslands().getNumOfGroups();
        return nIslands <= 3;
    }

    /**
     * Helper method that calculates whether a player in the game has no remaining towers in his school board.
     *
     * @return whether a player has no towers in his school board
     */
    private boolean playerHasNoTowers() {
        for (Player p : game.getPlayers()) {
            if (p.getNumOfTowers() == 0)
                return true;
        }
        return false;
    }

    /**
     * It returns an array of {@link Player} instances corresponding to the winners of the game.
     *
     * @return an array containing the winning player(s)
     */
    public List<Player> getWinners() {
        List<Player> winners = new ArrayList<>();
        for (Player p : game.getPlayers()) {
            if (p.getNumOfTowers() == 0) {
                winners.add(p);
                return winners;
            }
        }

        if (game.getIslands().getNumOfIslands() <= 3) {
            Player winner = game.getPlayers().stream()
                            .min((x, y) -> x.getNumOfTowers() < y.getNumOfTowers());
            winners.add(winner);
        }
        return winners;
    }
}
