package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;

import java.util.*;

/**
 * This class represents a place on the game board containing a certain number (also infinite, theoretically) of students.
 * Every instance of this class is immutable, and a copy of the instance is created after every operation, such as
 * adding or removing students.
 *
 * @author Leonardo Bianconi
 * @see StudentContainerInterface
 * @see BoundedStudentContainer
 * @see Hall
 */
final class StudentContainer implements StudentContainerInterface {

    /**
     * An EnumMap mapping every {@link PieceColor} to a Stack containing all the students of that color in the Container.
     */
    private final EnumMap<PieceColor, Stack<Student>> students;

    /**
     * A constructor that creates an empty Container (i.e. a Container with no students in it).
     */
    StudentContainer() {
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
    StudentContainer(StudentContainer oldContainer) throws IllegalArgumentException {
        if (oldContainer == null)
            throw new IllegalArgumentException("oldContainer must not be null");

        this.students = oldContainer.students.clone();
        for (PieceColor c : this.students.keySet()) {
            this.students.computeIfPresent(c, (k, v) -> (Stack<Student>) v.clone());
        }
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
        if (color == null) throw new IllegalArgumentException("color shouldn't be null");
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
     * This method allows to add a {@link Student} to the container, and returns a copy of the container containing
     * that student.
     *
     * @param s the student to add
     * @return the new container instance including the new student
     * @throws IllegalArgumentException if the student to add is null
     */
    @Override
    public StudentContainer add(Student s) throws IllegalArgumentException {
        if (s == null)
            throw new IllegalArgumentException("The student to add must not be null");

        StudentContainer c = new StudentContainer(this);
        c.students.get(s.getColor()).add(s);

        return c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<StudentContainer, Student> remove() throws EmptyContainerException {
        if (size() == 0) throw new EmptyContainerException();
        StudentContainer c = new StudentContainer(this);
        Student removedStudent = c.students.get(pickRandomColor()).pop();

        return new Tuple<>(c, removedStudent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tuple<StudentContainer, Student> remove(PieceColor color) throws EmptyContainerException, EmptyStackException, IllegalArgumentException {
        if (color == null) throw new IllegalArgumentException("Color must not be null");
        if (size() == 0) throw new EmptyContainerException();

        StudentContainer c = new StudentContainer(this);
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

        Random r = new Random();
        List<PieceColor> colors = students.values().stream()
                .flatMap(Collection::stream)
                .map(Student::getColor)
                .toList();

        return colors.get(r.nextInt(colors.size()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "StudentContainer{" +
                "students=" + students +
                '}';
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StudentContainer that = (StudentContainer) o;
        return this.students.entrySet().stream()
                .map(e -> that.students.get(e.getKey()).size() == e.getValue().size())
                .reduce(true, (a, b) -> a && b);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(students);
    }
}
