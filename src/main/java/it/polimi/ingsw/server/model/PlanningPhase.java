package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;

/**
 * This class represents the game state in which a player must play an assistant card.
 * It keeps track of whether the assistant has been played by the player, and waits for this condition to evolve to a
 * new phase.
 * @author Leonardo Bianconi
 * @see Phase
 */
public class PlanningPhase extends Phase {

    private final PlayerListIterator iterator;
    private Player curPlayer;
    private boolean playedAssistant;

    /**
     * The default constructor.
     * @param g a reference to the {@link Game} instance
     */
    protected PlanningPhase(Game g) {
        super(g);
    }

    /**
     * This constructor allows specifying which player is the first to choose an assistant.
     * @param g a reference to the {@link Game} instance
     * @param startingPlayer the first player to choose an assistant
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
