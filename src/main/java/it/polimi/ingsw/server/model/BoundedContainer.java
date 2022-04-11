package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a Container of students with a fixed maximum number of Students that it can contain. This
 * behaviour is typical of different game entities, e.g. the school entrance or the Sack.
 *
 * @author Leonardo Bianconi
 */
final class BoundedContainer implements ContainerInterface {
    /**
     * The maximum number of students that can be inside the Container.
     */
    private final int maxSize;

    /**
     * An EnumMap mapping every {@link PieceColor} to a Stack containing all the students of that color in the Container.
     */
    private final EnumMap<PieceColor, Stack<Student>> students;

    /**
     * A constructor that allows to create the BoundedContainer object specifying the maximum number of students.
     *
     * @param maxSize the maximum number of students
     * @throws IllegalArgumentException if the maxSize passed is lower or equals to zero
     */
    BoundedContainer(int maxSize) throws IllegalArgumentException {
        if (maxSize <= 0)
            throw new IllegalArgumentException("The bound of the container must be greater than zero");

        this.maxSize = maxSize;
        students = new EnumMap<PieceColor, Stack<Student>>(PieceColor.class);
        for (PieceColor color : PieceColor.values()) {
            students.put(color, new Stack<>());
        }
    }

    /**
     * BoundedContainer constructor that creates a copy of the one passed via parameter.
     *
     * @param oldContainer the BoundedContainer to copy
     * @throws IllegalArgumentException if che container passed is null
     */
    BoundedContainer(BoundedContainer oldContainer) throws IllegalArgumentException {
        if (oldContainer == null) throw new IllegalArgumentException("Old container must not be null.");

        this.maxSize = oldContainer.maxSize;
        this.students = oldContainer.students.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return students.values()
                .stream()
                .mapToInt(Vector::size)
                .sum();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(PieceColor color) {
        return students.get(color).size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Student> getStudents() {
        return students.values()
                .stream()
                .flatMap(s -> s.stream())
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BoundedContainer add(Student s) throws IllegalArgumentException, ContainerIsFullException {

        if (s == null) throw new IllegalArgumentException("Student to add must not be null.");
        if (size() == maxSize) throw new ContainerIsFullException();

        BoundedContainer c = new BoundedContainer(this);
        c.students.get(s.getColor()).add(s);
        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<BoundedContainer, Student> remove() throws EmptyContainerException {
        BoundedContainer c = new BoundedContainer(this);
        Student removedStudent = c.students.get(pickRandomColor()).pop();

        return new Tuple<>(c, removedStudent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<BoundedContainer, Student> remove(PieceColor color) throws EmptyStackException, IllegalArgumentException {
        if (color == null) throw new IllegalArgumentException("Color must not be null");

        BoundedContainer c = new BoundedContainer(this);
        Student removedStudent = c.students.get(color).pop();

        return new Tuple<>(c, removedStudent);

    }

    /**
     * Helper method that returns the PieceColor of a random student inside the BoundedContainer.
     *
     * @return the student's PieceColor
     * @throws EmptyContainerException if the BoundedContainer is empty
     */
    private PieceColor pickRandomColor() throws EmptyContainerException {

        if (size() == 0)
            throw new EmptyContainerException();

        Random r = new Random();
        List<PieceColor> colors = students.values().stream()
                .flatMap(Collection::stream)
                .map(Student::getColor)
                .collect(Collectors.toList());

        return colors.get(r.nextInt(colors.size()));
    }
}