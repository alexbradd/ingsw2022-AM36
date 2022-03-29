package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;

/**
 *
 * @author Leonardo Bianconi
 * @see Phase
 */
public class PreparePhase extends Phase {
    private final PlayerListIterator iterator;
    private Player curPlayer;
    private boolean chosenMage;
    private final Set<Mage> chosenMages;

    /**
     *
     * @param game
     */
    protected PreparePhase(Game game) {
        super(game);
    }

    /**
     *{@inheritDoc}
     */
    @Override
    public Phase doPhase() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void chooseMageDeck(String username, int id) throws OperationNotSupportedException, IndexOutOfBoundsException, MageAlreadyChosenException, NullPointerException, PlayerNotInGameException {
        super.chooseMageDeck(username, id);
    }

    /**
     *
     * @return
     */
    private Student getStudentFromSack() {}

    /**
     *
     */
    private void putTwoOfEachInSack() {}

    /**
     *
     */
    private void placeMnAndDistributeStudentsOnIslands() {}

    /**
     *
     */
    private void finishFillingSack() {}

    /**
     *
     */
    private void distributeResources() {}

}
