package it.polimi.ingsw.server.model;

/**
 * Represents a destination of the {@link Student} movement flow.
 *
 * @author Alexandru Gabriel Bradatan
 */
public interface StudentMoveDestination {
    /**
     * Add the given {@link Student} to the internal store.
     *
     * @param student the {@link Student} to add to the store
     * @throws NullPointerException if {@code student} is null
     */
    void receiveStudent(Student student);

    /**
     * Returns true if receiving a {@link Student} modifies the {@link Professor} assignments.
     *
     * @return true if receiving a {@link Student} modifies the {@link Professor} assignments, false otherwise
     * @see Professor
     */
    boolean requiresProfessorAssignment();
}
