package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import javax.naming.OperationNotSupportedException;
import java.util.Objects;

/**
 * This phase represents the first action a player must perform at the beginning of any action phase: moving
 * a certain amount of students (specified by the game rules) from their Entrance to another location (Island or
 * their Hall). It keeps track of how many students have been moved and moves on to a new {@link MNMovePhase} if
 * the amount of movements needed is performed.
 *
 * @author Leonardo Bianconi
 * @see ActionPhase
 * @see MNMovePhase
 */

public class StudentMovePhase extends ActionPhase {
    /**
     * The number of students already moved by the player.
     */
    private int numStudentsMoved;

    /**
     * The base constructor. It automatically chooses the first player to play an ActionPhase, i.e. the player that chose
     * the assistant with the minimum value (see game rules).
     *
     * @param g the {@link Game} instance
     */
    protected StudentMovePhase(Game g) {
        super(g);
        iterator = game.getPlayers().assistantValueIterator();
        curPlayer = iterator.next();
    }

    /**
     * This constructor creates the StudentMovePhase of a specific player and a specific order of next players to play.
     *
     * @param g             the {@link Game} instance
     * @param iterator      the {@link PlayerListIterator} instance, corresponding to next players to play
     */
    protected StudentMovePhase(Game g, PlayerListIterator iterator) {
        super(g);
        this.iterator = iterator;
        curPlayer = this.iterator.next();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Entrance getPlayerEntrance(String username) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException {
        return super.getPlayerEntrance(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Hall getPlayerHall(String username) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException {
        return super.getPlayerHall(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase doPhase() {
        while (numStudentsMoved < game.getnStudentsMovable()) {
            try {
                game.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new MNMovePhase(game, iterator, curPlayer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveStudent(PieceColor color, StudentMoveSource source, StudentMoveDestination destination) throws OperationNotSupportedException, NullPointerException, IllegalArgumentException {
        Objects.requireNonNull(color, "color must not be null");
        Objects.requireNonNull(source, "source must not be null");
        Objects.requireNonNull(destination, "destination must not be null");

        if (!source.equals(curPlayer.getEntrance()))
            throw new IllegalArgumentException("the student must be moved from the player's entrance");
        if (!destination.equals(curPlayer.getHall()) || //TODO)
            throw new IllegalArgumentException("the student must be moved from the player's hall");

        try {
            Student s = curPlayer.getEntrance().sendStudent(color);
        } catch (Exception e) {
            throw new IllegalArgumentException("no students of the specified color to move from source");
        }
        destination.receiveStudent(s);

        synchronized(game) {
            numStudentsMoved++;
        }
        game.notifyAll();
    }
}
