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
    private Set<Student> students;

    /**
     * The maximum number of students that can be present on the cloud.
     */
    private final int maxSize;

    /**
     * Cloud constructor.
     * There are no students present on the cloud initially.
     *
     * @param maxSize maximumm cloud size
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
     * Creates a shallow copy of the given cloud
     *
     * @param old the Cloud to copy
     */
    Cloud(Cloud old) {
        this.students = old.students;
        this.maxSize = old.maxSize;
    }

    /**
     * Returns a copy of the students set while emptying the original.
     *
     * @return a tuple containing the Update cloud and the set of Students
     */
    Tuple<Cloud, Set<Student>> drainCloud() {
        return new Tuple<>(new Cloud(this.maxSize), new HashSet<>(this.students));
    }

    /**
     * Refills the cloud with new students.
     *
     * @param students new students set
     * @return a new Updated Cloud
     * @throws IllegalArgumentException students should not be null, the new students size is larger than the allowed maxSize
     */
    Cloud refillCloud(Set<Student> students) throws IllegalArgumentException {
        if (students == null) {
            throw new IllegalArgumentException("students should not be null");
        }
        if (students.size() <= maxSize) {
            Cloud c = new Cloud(this);
            c.students = new HashSet<>(students);
            return c;
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
