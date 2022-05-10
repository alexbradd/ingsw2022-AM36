package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.exceptions.ColorIsFullException;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;

import java.util.*;

/**
 * This class represents a Container of students with a fixed maximum number of Students that it can contain. This
 * behaviour is typical of different game entities, e.g. the school entrance or the Sack.
 *
 * @author Leonardo Bianconi
 */
final class BoundedStudentContainer implements StudentContainerInterface {
    /**
     * The maximum number of students that can be inside the Container.
     */
    private final int maxSize;

    /**
     * An internal StudentContainer used for storing students.
     */
    private StudentContainer wrapped;

    /**
     * A constructor that allows to create the BoundedContainer object specifying the maximum number of students.
     *
     * @param maxSize the maximum number of students
     * @throws IllegalArgumentException if the maxSize passed is lower or equals to zero
     */
    BoundedStudentContainer(int maxSize) throws IllegalArgumentException {
        if (maxSize <= 0)
            throw new IllegalArgumentException("The bound of the container must be greater than zero");

        this.maxSize = maxSize;
        this.wrapped = new StudentContainer();
    }

    /**
     * BoundedContainer constructor that creates a copy of the one passed via parameter.
     *
     * @param oldContainer the BoundedContainer to copy
     * @throws IllegalArgumentException if che container passed is null
     */
    BoundedStudentContainer(BoundedStudentContainer oldContainer) throws IllegalArgumentException {
        if (oldContainer == null) throw new IllegalArgumentException("Old container must not be null.");

        this.maxSize = oldContainer.maxSize;
        this.wrapped = oldContainer.wrapped;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return wrapped.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(PieceColor color) {
        return wrapped.size(color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Student> getStudents() {
        return wrapped.getStudents();
    }

    /**
     * This method allows to add a {@link Student} to the container, and returns a copy of the container containing
     * that student.
     *
     * @param s the student to add
     * @return the new container instance including the new student
     * @throws IllegalArgumentException if the student to add is null
     * @throws ContainerIsFullException if the container reached the maximum size
     * @throws ColorIsFullException     never thrown
     */
    @Override
    public BoundedStudentContainer add(Student s) throws IllegalArgumentException, ContainerIsFullException {
        if (s == null) throw new IllegalArgumentException("Student to add must not be null.");
        if (size() == maxSize) throw new ContainerIsFullException();

        BoundedStudentContainer c = new BoundedStudentContainer(this);
        c.wrapped = c.wrapped.add(s);
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<BoundedStudentContainer, Student> remove() throws EmptyContainerException {
        BoundedStudentContainer c = new BoundedStudentContainer(this);
        return c.wrapped
                .remove()
                .map(t -> {
                    c.wrapped = t.getFirst();
                    return new Tuple<>(c, t.getSecond());
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<BoundedStudentContainer, Student> remove(PieceColor color) throws EmptyContainerException, EmptyStackException, IllegalArgumentException {
        if (color == null) throw new IllegalArgumentException("Color must not be null");

        BoundedStudentContainer c = new BoundedStudentContainer(this);
        return c.wrapped
                .remove(color)
                .map(t -> {
                    c.wrapped = t.getFirst();
                    return new Tuple<>(c, t.getSecond());
                });

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "BoundedStudentContainer{" +
                "maxSize=" + maxSize +
                ", wrapped=" + wrapped +
                '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundedStudentContainer that = (BoundedStudentContainer) o;
        return maxSize == that.maxSize && Objects.equals(wrapped, that.wrapped);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(maxSize, wrapped);
    }
}
