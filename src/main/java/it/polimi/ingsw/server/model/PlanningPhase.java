package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.AssistantAlreadyPlayedException;
import it.polimi.ingsw.server.model.exceptions.AssistantNotInDeckException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import javax.naming.OperationNotSupportedException;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the game state in which a player must play an assistant card.
 * It keeps track of whether the assistant has been played by the player, and waits for this condition to evolve to a
 * new phase.
 *
 * @author Leonardo Bianconi
 * @see Phase
 */
public class PlanningPhase extends Phase {

    private final PlayerListIterator iterator;
    private final Player curPlayer;
    private boolean playedAssistant;
    private final Set<Integer> playedThisTurn;

    /**
     * The default constructor. Used for the first player's PlanningPhase
     *
     * @param g a reference to the {@link Game} instance
     */
    protected PlanningPhase(Game g) {

        super(g);
        iterator = game.getPlayers().clockWiseIterator(0);  //TODO
        curPlayer = iterator.next();
        playedAssistant = false;
        playedThisTurn = new HashSet<>();
    }

    /**
     * This constructor allows specifying which player is the first to choose an assistant.
     *
     * @param g              a reference to the {@link Game} instance
     * @param iterator       a {@link PlayerListIterator} starting from the player to choose the assistant
     * @param playedThisTurn a Set containing the values of all the assistants played in this turn
     */
    public PlanningPhase(Game g, PlayerListIterator iterator, Set<Integer> playedThisTurn) {
        super(g);
        this.iterator = iterator;
        curPlayer = this.iterator.next();
        playedAssistant = false;
        this.playedThisTurn = playedThisTurn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase doPhase() {
        while (!playedAssistant) {
            try {
                game.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (iterator.hasNext()) {
            return new PlanningPhase(game, iterator, playedThisTurn);
        }
        return new StudentMovePhase(game);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO
    public void playAssistant(String username, int id) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, AssistantAlreadyPlayedException, AssistantNotInDeckException, IndexOutOfBoundsException {
        if (username == null)
            throw new NullPointerException("username must not be null");

        if (id <= 0 || id > 10)
            throw new IndexOutOfBoundsException("assistant value out of bounds");

        synchronized (game) {
            if (!username.equals(curPlayer.getUsername()))
                throw new InvalidPlayerException();

            if (playedThisTurn.contains(id) && !forcedToPlayAssistant(curPlayer, id))
                throw new AssistantAlreadyPlayedException();

            //if (!curPlayer.getAssistants().containsInt(id))
            //    throw new AssistantNotInDeckException();

            try {
                curPlayer.playAssistant(id);
            } catch (IndexOutOfBoundsException e) {
                throw new AssistantAlreadyPlayedException();
            }

            playedThisTurn.add(id);
            playedAssistant = true;

            game.notifyAll();
        }
    }

    private boolean forcedToPlayAssistant(Player player, int id) {
        for (Assistant a : player.getAssistants()) {
            if (!playedThisTurn.contains(a.getOrderValue()))
                return false;
        }
        return true;
    }

}
