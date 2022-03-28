package it.polimi.ingsw.server.model;

import java.util.*;

/**
 * This class models the game's sack.
 *
 * @author Mattia Busso
 */
public class Sack implements StudentMoveDestination {

    /**
     * List containing the students inside the sack.
     */
    private final List<Student> students;

    /**
     * Sack constructor.
     */
    Sack() {
        students = new ArrayList<>();
    }

    /**
     * Returns the number of students inside the sack.
     *
     * @return size of students attribute
     */
    int size() {
        return students.size();
    }

    /**
     * Pops a random student from the sack and returns it.
     *
     * @return student
     * @throws IllegalStateException can't use this method if sack is currently empty.
     */
    Student sendStudent() throws IllegalStateException {
        Random r = new Random();
        Student random_student;
        if (size() == 0) {
            throw new IllegalStateException("Can't get student from empty student collection");
        }
        int random_index = r.nextInt(size());
        random_student = students.get(random_index);
        students.remove(random_student);
        return random_student;
    }

    /**
     * Adds a given student to the sack.
     *
     * @param student
     * @throws IllegalArgumentException student should not be null
     * @see StudentMoveDestination
     */
    @Override
    public void receiveStudent(Student student) throws IllegalArgumentException {
        if (student == null) {
            throw new IllegalArgumentException("student should not be null");
        }
        students.add(student);
    }

    /**
     * @return boolean
     * @see StudentMoveDestination
     */
    @Override
    public boolean requiresProfessorAssignment() {
        return false;
    }

}
