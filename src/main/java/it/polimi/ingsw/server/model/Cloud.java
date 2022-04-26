package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.ColorIsFullException;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;

import java.util.*;

/**
 * This class models the game's clouds.
 *
 * @author Mattia Busso, Alexandru Gabriel Bradatan
 */
class Cloud implements StudentContainerInterface {

    /**
     * This cloud's container
     */
    private BoundedStudentContainer students;

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
        students = new BoundedStudentContainer(maxSize);
    }

    /**
     * Creates a shallow copy of the given cloud
     *
     * @param old the Cloud to copy
     */
    Cloud(Cloud old) {
        this.maxSize = old.maxSize;
        this.students = old.students;
    }

    /**
     * Returns a copy of the students set while emptying the original.
     *
     * @return a tuple containing the Update cloud and the set of Students
     */
    Tuple<Cloud, Set<Student>> drainCloud() {
        return new Tuple<>(new Cloud(this.maxSize), this.students.getStudents());
    }

    /**
     * Returns the number of students stored in this cloud
     *
     * @return the number of students stored in this cloud
     */
    @Override
    public int size() {
        return this.students.size();
    }

    /**
     * It returns the number of Students of a certain color inside the container.
     *
     * @param color the color of the Students
     * @return the number of Students of that color inside the container
     */
    @Override
    public int size(PieceColor color) {
        return students.size(color);
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
            for (Student s : students)
                c = c.add(s);
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
    @Override
    public Set<Student> getStudents() {
        return students.getStudents();
    }

    /**
     * This method removes a random {@link Student} from the container, and returns it with a new container instance, not
     * containing the removed student.
     *
     * @return A {@link Tuple}<Container, Student> with the new container instance and the removed student
     * @throws EmptyContainerException if the container is empty
     */
    @Override
    public Cloud add(Student s) throws IllegalArgumentException, ContainerIsFullException, ColorIsFullException {
        Cloud c = new Cloud(this);
        c.students = c.students.add(s);
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<Cloud, Student> remove() throws EmptyContainerException {
        Cloud c = new Cloud(this);
        Tuple<BoundedStudentContainer, Student> t = c.students.remove();
        c.students = t.getFirst();
        return t.map((container, s) -> new Tuple<>(c, t.getSecond()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<Cloud, Student> remove(PieceColor color) throws IllegalArgumentException, EmptyStackException {
        Cloud c = new Cloud(this);
        Tuple<BoundedStudentContainer, Student> t = c.students.remove(color);
        c.students = t.getFirst();
        return t.map((container, s) -> new Tuple<>(c, t.getSecond()));
    }
}
