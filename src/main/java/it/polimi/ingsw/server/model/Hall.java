package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.ColorIsFullException;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class models the game's hall.
 *
 * @author Mattia Busso, Leonardo Bianconi
 */
public class Hall implements ContainerInterface {

    /**
     * The maximum size of every single color row of the Hall
     */
    public final static int maxColorSize = 10;

    /**
     * The maximum total size of the hall, calculated as {@code maxColorSize} times the number of colors
     */
    public final static int maxSize = maxColorSize * PieceColor.values().length;

    /**
     * An EnumMap mapping every {@link PieceColor} to a Stack containing all the students of that color in the Container.
     */
    private final EnumMap<PieceColor, Stack<Student>> students;

    /**
     * Hall constructor.
     */
    Hall() {
        students = new EnumMap<>(PieceColor.class);
        for (PieceColor color : PieceColor.values()) {
            students.put(color, new Stack<>());
        }
    }

    /**
     * A Hall constructor that creates a shallow copy of the one passed via parameter.
     *
     * @param oldHall the Hall to copy
     * @throws IllegalArgumentException if the hall passed is null
     */
    Hall(Hall oldHall) throws IllegalArgumentException {
        if (oldHall == null) throw new IllegalArgumentException("The old hall must not be null.");
        students = oldHall.students.clone();
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
    public Hall add(Student s) throws IllegalArgumentException, ContainerIsFullException, ColorIsFullException {
        if (s == null) throw new IllegalArgumentException("Student to add must not be null.");
        if (size() == maxSize) throw new ContainerIsFullException();
        if (size(s.getColor()) == maxColorSize) throw new ColorIsFullException();

        Hall h = new Hall(this);
        h.students.get(s.getColor()).add(s);
        return h;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<Hall, Student> remove() throws EmptyContainerException {
        Hall h = new Hall(this);
        Student removedStudent = h.students.get(pickRandomColor()).pop();

        return new Tuple<>(h, removedStudent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<Hall, Student> remove(PieceColor color) throws EmptyStackException , IllegalArgumentException {
        if (color == null) throw new IllegalArgumentException("Color must not be null");

        Hall h = new Hall(this);
        Student removedStudent = h.students.get(color).pop();

        return new Tuple<>(h, removedStudent);
    }

    /**
     * Helper method that returns the PieceColor of a random student inside the Hall.
     *
     * @return the student's PieceColor
     * @throws EmptyContainerException if the Hall is empty
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
