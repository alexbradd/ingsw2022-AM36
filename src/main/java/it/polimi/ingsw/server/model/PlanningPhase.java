package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;

/**
 * @author Leonardo Bianconi
 * @see Phase
 */
public class PlanningPhase extends Phase {

    private final PlayerListIterator iterator;
    private Player curPlayer;
    private boolean playedAssistant;

    /**
     *
     * @param g
     */
    protected PlanningPhase(Game g) {
        super(g);
    }

    /**
     *
     * @param g
     * @param startingPlayer
     */
    public PlanningPhase(Game g, int startingPlayer) {
        super(g);
        // TODO
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
    public void playAssistant(String username, int id) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, AssistantAlreadyPlayedException, AssistantNotInDeckException, IndexOutOfBoundsException {
        super.playAssistant(username, id);
    }
}
