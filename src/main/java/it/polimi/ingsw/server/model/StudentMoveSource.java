package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;

/**
 * Represents a source of the {@link Student} movement flow.
 *
 * @author Alexandru Gabriel Bradatan
 */
public interface StudentMoveSource {
    /**
     * Remove and return a {@link Student} of the given color from the store.
     *
     * @param color {@link Student}'s color to send
     * @return a {@link Student} from the store
     */
    Student sendStudent(PieceColor color);

    /**
     * Returns true if sending a {@link Student} modifies the {@link Professor} assignments.
     * @return true if sending a {@link Student} modifies the {@link Professor} assignments
     */
    boolean requiresProfessorAssignment();
}
