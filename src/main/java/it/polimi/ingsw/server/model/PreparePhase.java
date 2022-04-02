package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;
import java.util.ArrayList;
import java.util.HashSet;
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
     * The number of chosen mages.
     */
    private int nChosenMages;
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
        iterator = game.getPlayers().clockwiseiterator(0);
        curPlayer = iterator.next();
        nChosenMages = 0;
        chosenMages = new HashSet<>();  //TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase doPhase() {
        while (nChosenMages < game.getnPlayers()) {
            try {
                game.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        setupGame();
        return new PlanningPhase(game);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void chooseMageDeck(String username, String mage) throws OperationNotSupportedException, IndexOutOfBoundsException, MageAlreadyChosenException, NullPointerException, InvalidPlayerException {
        if (username == null)
            throw new NullPointerException("username must not be null");
        if (mage == null)
            throw new NullPointerException("mage must not be null");

        Mage mageObject = Mage.valueOf(mage);

        synchronized (game) {
            if (username != curPlayer.getUsername())
                throw new InvalidPlayerException();
            for (Mage m : chosenMages) {
                if (m.equals(mageObject))
                    throw new MageAlreadyChosenException();
            }
            chosenMages.add(mageObject);
            nChosenMages++;
        }
        assignDeckToPlayer(game.getPlayers().get(username), mageObject);
        notifyAll();
    }

    /**
     * It returns a randomly chosen student from the sack.
     *
     * @return a {@link Student} instance
     */
    private Student getStudentFromSack() {

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
        for (int i = 0; i < game.getPlayers().size(); i++) {
            game.getPlayers().get(i).
        }
    }

    private void assignDeckToPlayer(Player player, Mage mage) {
        List<Assistant> deck = new ArrayList();
        for (int i = 1; i < 11; i++)
            deck.add(new Assistant(i, mage));
        player.receiveDeck(deck);
    }

    /**
     * Helper method that sets up all game entities with the correct values for a game.
     */
    // TODO replace hardcoded ints with constants
    private void setupGame() {

        //send mn on island 0
        Island firstIsland = game.getIslands().get(0);
        game.getMotherNature().assignStartingIsland(firstIsland);

        // place professors on the table
        for (int i = 0; i < PieceColor.values().length; i++) {
            professor = new Professor(PieceColor.values()[i]);
        }

        // populate sack 1
        for (int i = 0; i < game.getProfessors().length; i++) {
            for (int j = 0; j < 2; j++) {
                Student student = new Student(game.getProfessors()[i]);
                game.getSack().receiveStudent(student);
            }
        }

        //send students on islands
        for (int i = 0; i < 12; i++) {
            if (i != 0 || i != 6) {
                Student randomStudent = game.getSack().sendStudent();
                game.getIslands().get(i).receiveStudent(randomStudent);
            }
        }

        //populate sack 2
        for (int i = 0; i < game.getProfessors().length; i++) {
            for (int j = 0; j < 24; j++) {
                Student student = new Student(game.getProfessors()[i]);
                game.getSack().receiveStudent(student);
            }
        }

        // place clouds
        int cloudSize = 3;
        if (game.getnPlayers() == 3) cloudSize = 4;

        for (int i = 0; i < game.getnPlayers(); i++) {
            Cloud cloud = new Cloud(cloudSize);
            game.getClouds().add(cloud);
        }


        for (PlayerListIterator iterator = game.getPlayers().clockWiseIterator(0); iterator.hasNext(); ) {
            Player p = iterator.next();

            // place towers
            for (int j = 0; j < game.getnTowers(); j++)
                p.receiveTower(Player.getTowerColor());

            // place students in Hall
            for (int j = 0; j < game.getnStudentsEntrance()) {
                p.getHall().receiveStudent(game.getSack().sendStudent());
            }
        }
    }
}


