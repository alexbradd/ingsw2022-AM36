package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;
import java.util.Set;

/**
 * This phase represents the last action a player must perform during his turn: picking a cloud full of students from the
 * table and moving all of these students to its Entrance. After that, the game evolves to a new phase, depending on whether
 * the current player is the last of this turn or not.
 *
 * @author Leonardo Bianconi
 * @see MNMovePhase
 * @see ActionPhase
 */
public class CloudPickPhase extends ActionPhase {
    /**
     * Whether the cloud has already been picked by the player.
     */
    private boolean cloudPicked;

    /**
     * This constructor creates the CloudPickPhase of a specific player and a specific order of next players to play.
     *
     * @param g             the {@link Game} instance
     * @param iterator      the {@link PlayerListIterator} instance, corresponding to next players to play
     * @param currentPlayer the {@link Player} instance, corresponding to the current player
     */
    protected CloudPickPhase(Game g, PlayerListIterator iterator, Player currentPlayer) {
        super(g);
        this.iterator = iterator;
        this.curPlayer = currentPlayer;
        cloudPicked = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase doPhase() {
        while(!cloudPicked) {
            try {
                game.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (checkWin())
            return new EndgamePhase(game, getWinners());
        else if (iterator.hasNext())
            return new StudentMovePhase(game, iterator);

        return new PlanningPhase(game);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pickCloud(String username, int id) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, IndexOutOfBoundsException, CloudAlreadyChosenException {
        if (username == null)
            throw new NullPointerException("username must not be null");
        if (!username.equals(curPlayer.getUsername()))
            throw new InvalidPlayerException();
        if (id < 0 || id >= game.getnPlayers())
            throw new IndexOutOfBoundsException("not a valid id for a cloud");

        Cloud cloud = game.getClouds().get(id);
        Set<Student> studentsOnCloud = cloud.getStudents();

        if (studentsOnCloud.isEmpty()) {
            throw new CloudAlreadyChosenException();
        } else {
            for (Student s : cloud.drainCloud()) {
                curPlayer.getEntrance().receiveStudent(s);
            }
        }
        synchronized (game) {
            cloudPicked = true;
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
