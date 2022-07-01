package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.iterators.AssistantValueIterator;

/**
 * This phase represents the first action a player must perform at the beginning of any action phase: moving
 * a certain amount of students (specified by the game rules) from their Entrance to another location (Island or
 * their Hall). It keeps track of how many students have been moved and moves on to a new {@link MnMovePhase} if
 * the amount of movements needed is performed.
 *
 * @author Leonardo Bianconi, Alexandru Gabriel Bradatan
 * @see ActionPhase
 * @see MnMovePhase
 */
public class StudentMovePhase extends ActionPhase {
    /**
     * This phase's AssistantValueIterator. It will be passed untouched to the following MnMovePhase
     */
    private final AssistantValueIterator avi;
    /**
     * Number of students the current player has moved so far
     */
    private int movedSoFar;

    /**
     * Creates a new StudentMovePhase with the given {@link Table} and {@link Player}.
     *
     * @param prev    the previous Phase that led to the creation of this one
     * @param avi     an {@link AssistantValueIterator}
     * @param current the current {@link Player} of this ActionPhase
     * @throws IllegalArgumentException if any parameter is null
     */
    StudentMovePhase(Phase prev, AssistantValueIterator avi, Player current) {
        super(prev, current);
        if (avi == null) throw new IllegalArgumentException("avi shouldn't be null");
        movedSoFar = 0;
        this.avi = new AssistantValueIterator(avi);
    }

    /**
     * Creates a shallow copy of the given StudentMovePhase
     *
     * @param old the StudentMovePhase to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private StudentMovePhase(StudentMovePhase old) {
        super(old);
        movedSoFar = old.movedSoFar;
        this.avi = old.avi;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase markStudentMove(Player player) throws InvalidPhaseUpdateException {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        StudentMovePhase s = this.shallowCopy();
        s.movedSoFar++;
        if (s.checkWin())
            return new EndgamePhase(s);
        if (s.movedSoFar >= getParameters().getnStudentsMovable())
            return new MnMovePhase(s, s.avi);
        return s;
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    StudentMovePhase shallowCopy() {
        return new StudentMovePhase(this);
    }
}
