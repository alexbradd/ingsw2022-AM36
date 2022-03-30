package it.polimi.ingsw.server.model;

import java.util.*;

/**
 * This class models the game's clouds.
 *
 * @author Mattia Busso
 */
class Cloud {

    /**
     * Set of students on the cloud.
     */
    private final Set<Student> students;

    /**
     * The maximum number of students that can be present on the cloud.
     */
    private final int maxSize;

    /**
     * Cloud constructor.
     * There are no students present on the cloud initially.
     *
     * @param maxSize
     * @throws IllegalArgumentException maxSize can't be <= 0
     */
    Cloud(int maxSize) throws IllegalArgumentException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize can't be <= 0");
        }
        this.maxSize = maxSize;
        students = new HashSet<>();
    }

    /**
     * Returns a copy of the students set while emptying the original.
     *
     * @return the students set
     */
    Set<Student> drainCloud() {
        Set<Student> temp = new HashSet<>(students);
        students.clear();
        return temp;
    }

    /**
     * Refills the cloud with new students.
     *
     * @param students new students set
     * @throws IllegalArgumentException students should not be null, the new students size is larger than the allowed maxSize
     */
    void refillCloud(Set<Student> students) throws IllegalArgumentException {
        if (students == null) {
            throw new IllegalArgumentException("students should not be null");
        }
        if (students.size() <= maxSize) {
            this.students.clear();
            this.students.addAll(students);
        } else {
            throw new IllegalArgumentException("the new students size is larger than the allowed maxSize");
        }
    }

    /**
     * Returns a copy of the current students on the cloud.
     *
     * @return students_copy
     */
    Set<Student> getStudents() {
        return new HashSet<>(students);
    }

}
