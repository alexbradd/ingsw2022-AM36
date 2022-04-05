package it.polimi.ingsw.server.model;

import java.util.List;

/**
 * This phase represents the very last state of a game: the one in which a winner has been found and the game has ended.
 * This phase has no particular operations: the server sends a message telling all clients the winner for this game, and
 * shuts down after a timeout.
 *
 * @author Leonardo Bianconi
 * @see Phase
 */
public class EndgamePhase extends Phase {
    /**
     * A list containing the winning {@link Player}(s).
     */
    private List<Player> winners;

    /**
     * The base constructor.
     *
     * @param game    the {@link Game} instance
     * @param winners the array of winning {@link Player}(s)
     */
    protected EndgamePhase(Game game, List<Player> winners) {
        super(game);
        this.winners = winners;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase doPhase() {
        game.setEnded();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //TODO
        for (Player p : winners)
            System.out.println(p + "has won the game.");

        return null;
    }
}
