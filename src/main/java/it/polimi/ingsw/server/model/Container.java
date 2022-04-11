package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class represents a place on the game board containing a certain number (also infinite, theoretically) of students.
 * Every instance of this class is immutable, and a copy of the instance is created after every operation, such as
 * adding or removing students.
 *
 * @author Leonardo Bianconi
 * @see ContainerInterface
 * @see BoundedContainer
 * @see Hall
 */
final class Container implements ContainerInterface {

    /**
     * An EnumMap mapping every {@link PieceColor} to a Stack containing all the students of that color in the Container.
     */
    private final EnumMap<PieceColor, Stack<Student>> students;

    /**
     * A constructor that creates an empty Container (i.e. a Container with no students in it).
     */
    Container() {
        students = new EnumMap<>(PieceColor.class);

        for (PieceColor color : PieceColor.values()) {
            students.put(color, new Stack<>());
        }
    }

    /**
     * A constructor that creates a shallow copy of the Container passed via parameter
     *
     * @param oldContainer the Container to copy
     * @throws IllegalArgumentException if the Container to copy is null
     */
    Container(Container oldContainer) throws IllegalArgumentException {
        if (oldContainer == null)
            throw new IllegalArgumentException("oldContainer must not be null");

        this.students = oldContainer.students.clone();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return students.values().stream()
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
    public Set<Student> getStudents() {
        Set<Student> returnedStudents = new HashSet<>();
        for (Stack<Student> color : students.values())
            returnedStudents.addAll(color);

        return returnedStudents;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Container add(Student s) throws IllegalArgumentException {
        if (s == null)
            throw new IllegalArgumentException("The student to add must not be null");

        Container c = new Container(this);
        c.students.get(s.getColor()).add(s);

        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<Container, Student> remove() throws EmptyContainerException {
        Container c = new Container(this);
        Student removedStudent = c.students.get(pickRandomColor()).pop();

        return new Tuple<>(c, removedStudent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<Container, Student> remove(PieceColor color) throws EmptyStackException, IllegalArgumentException {
        if (color == null) throw new IllegalArgumentException("Color must not be null");

        Container c = new Container(this);
        Student removedStudent = c.students.get(color).pop();

        return new Tuple<>(c, removedStudent);
    }

    /**
     * Helper method that returns the PieceColor of a random student inside the Container.
     *
     * @return the student's PieceColor
     * @throws EmptyContainerException if the Container is empty
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
