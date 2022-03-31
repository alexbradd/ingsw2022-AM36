package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;
import java.util.Set;

/**
 * It represents the state of the game in which all the game instances are being populated. It also asks for all players
 * to choose their mage (assistant cards deck). It generally follows the {@link LobbyPhase}, it assumes that a lobby with
 * the correct amount of players is already connected. After this phase, the game environment should be fully ready
 * for playing.
 *
 * @author Leonardo Bianconi
 * @see Phase
 * @see LobbyPhase
 * @see PlanningPhase
 */
public class PreparePhase extends Phase {
    /**
     * An iterator that specifies the order for choosing the mage.
     */
    private final PlayerListIterator iterator;
    /**
     * The player that is currently choosing his mage.
     */
    private Player curPlayer;
    /**
     * Whether the mage has been chosen by all players.
     */
    private boolean chosenMage;
    /**
     * A set containing all already chosen mages.
     */
    private final Set<Mage> chosenMages;

    /**
     * The default constructor.
     *
     * @param game the game instance
     */
    protected PreparePhase(Game game) {
        super(game);
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
    public void chooseMageDeck(String username, int id) throws OperationNotSupportedException, IndexOutOfBoundsException, MageAlreadyChosenException, NullPointerException, PlayerNotInGameException {
        super.chooseMageDeck(username, id);
    }

    /**
     * It returns a randomly chosen student from the sack.
     * @return a {@link Student} instance
     */
    private Student getStudentFromSack() {
        return null;
    }

    /**
     * It puts two students of each color in the sack (see game rules - preparation).
     */
    private void putTwoOfEachInSack() {
    }

    /**
     * It places all the different game pieces (Mother Nature and students) on the islands as the initial configuration of
     * the game requires (see game rules).
     */
    private void placeMnAndDistributeStudentsOnIslands() {
    }

    /**
     * It places all the remaining students in the sack.
     */
    private void finishFillingSack() {
    }

    /**
     * It distributes all the needed initial game resources to all the connected players.
     */
    private void distributeResources() {
    }

}
