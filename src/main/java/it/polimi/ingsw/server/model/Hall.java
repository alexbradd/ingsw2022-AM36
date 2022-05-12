package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.ColorIsFullException;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;

import java.util.EmptyStackException;
import java.util.Objects;
import java.util.Set;

/**
 * This class models the game's hall.
 *
 * @author Mattia Busso, Leonardo Bianconi
 */
class Hall implements StudentContainerInterface {

    /**
     * The maximum size of every single color row of the Hall
     */
    public final static int maxColorSize = 10;

    /**
     * The maximum total size of the hall, calculated as {@code maxColorSize} times the number of colors
     */
    public final static int maxSize = maxColorSize * PieceColor.values().length;

    /**
     * An internal StudentContainer used for storing students.
     */
    private BoundedStudentContainer wrapped;

    /**
     * Hall constructor.
     */
    Hall() {
        wrapped = new BoundedStudentContainer(maxSize);
    }

    /**
     * A Hall constructor that creates a shallow copy of the one passed via parameter.
     *
     * @param oldHall the Hall to copy
     * @throws IllegalArgumentException if the hall passed is null
     */
    Hall(Hall oldHall) throws IllegalArgumentException {
        if (oldHall == null) throw new IllegalArgumentException("The old hall must not be null.");
        wrapped = oldHall.wrapped;
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
     * Returns true if the Hall is full
     *
     * @return true if the Hall is full
     */
    public boolean isFull() {
        return size() == maxSize;
    }

    /**
     * Returns true if the row of the specified color is full
     *
     * @return true if the row of the specified color is full
     * @throws IllegalArgumentException if {@code color} is null
     */
    public boolean isFull(PieceColor color) {
        if (color == null) throw new IllegalArgumentException("color shouldn't be null");
        return size(color) == maxColorSize;
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
     * @throws ContainerIsFullException if the container is already full (if there is a bound to the number of students)
     * @throws ColorIsFullException     if there are too many students of this color inside the container (if there is a
     *                                  bound to the number of students of the same color)
     */
    @Override
    public Hall add(Student s) throws IllegalArgumentException, ContainerIsFullException, ColorIsFullException {
        if (s == null) throw new IllegalArgumentException("Student to add must not be null.");
        if (size() == maxSize) throw new ContainerIsFullException();
        if (size(s.getColor()) == maxColorSize) throw new ColorIsFullException();

        Hall h = new Hall(this);
        h.wrapped = h.wrapped.add(s);
        return h;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<Hall, Student> remove() throws EmptyContainerException {
        Hall h = new Hall(this);
        return h.wrapped
                .remove()
                .map(t -> {
                    h.wrapped = t.getFirst();
                    return new Tuple<>(h, t.getSecond());
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<Hall, Student> remove(PieceColor color) throws EmptyContainerException, EmptyStackException, IllegalArgumentException {
        if (color == null) throw new IllegalArgumentException("Color must not be null");

        Hall h = new Hall(this);
        return h.wrapped
                .remove(color)
                .map(t -> {
                    h.wrapped = t.getFirst();
                    return new Tuple<>(h, t.getSecond());
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hall hall = (Hall) o;
        return Objects.equals(wrapped, hall.wrapped);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(wrapped);
    }
}
