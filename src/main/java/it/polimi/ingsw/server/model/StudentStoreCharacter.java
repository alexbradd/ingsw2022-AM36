package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.enums.PieceColor;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents and abstract character card that has the ability to store students a maximum amount of students. This
 * class simply provides functionality to the student handling methods provided in {@link Character}.
 *
 * @author Alexandru Gabriel Bradatan
 */
abstract class StudentStoreCharacter extends Character {
    /**
     * Map that associates every color with a stack of students of said color.
     */
    private final EnumMap<PieceColor, Stack<Student>> students;

    /**
     * Maximum amount of students storable on this card.
     */
    private final int maxStudentAmount;

    /**
     * Base constructor. Sets up the card's initial cost, character and maximum number of students that this card can
     * have.
     *
     * @param character        this card's character
     * @param initialCost      initial cost of the card
     * @param maxStudentAmount the maximum amount of {@link Student} that can be placed on the card.
     * @throws IllegalArgumentException if {@code initialCost} is less than zero
     * @throws IllegalArgumentException if {@code maxStudentAmount} is less than zero
     */
    StudentStoreCharacter(Characters character, int initialCost, int maxStudentAmount) {
        super(character, initialCost);
        if (maxStudentAmount < 0) throw new IllegalArgumentException("maxStudentAmount should be >= 0");
        students = new EnumMap<>(PieceColor.class);
        this.maxStudentAmount = maxStudentAmount;
    }

    /**
     * {@link PreparePhase} hook. This implementation additionally requests {@code maxStudentAmount} of {@link Student}
     * from the {@link Sack}. Subclasses should call this implementation if they want this behaviour.
     *
     * @param phase the {@link PreparePhase} the card's hook has been called from
     * @throws IllegalArgumentException if {@code phase} is null
     */
    @Override
    void doPrepare(PreparePhase phase) {
        super.doPrepare(phase);
        for (int i = 0; i < maxStudentAmount; i++)
            receiveStudent(phase.getSack().sendStudent());
    }

    /**
     * Returns the students currently placed on the card.
     *
     * @return the set of students placed on the card
     */
    @Override
    public Set<Student> getStudents() {
        return students.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Remove and return a {@link Student} of the given color from the store.
     *
     * @param color {@link Student}'s color to send
     * @return a {@link Student} from the store
     * @throws IllegalStateException if there are no students of that color to send
     */
    @Override
    public Student sendStudent(PieceColor color) {
        students.putIfAbsent(color, new Stack<>());
        try {
            return students.get(color).pop();
        } catch (EmptyStackException e) {
            throw new IllegalStateException("Cannot send any more students of color " + color, e);
        }
    }

    /**
     * Adds the given {@link Student} to the internal store.
     *
     * @param student the {@link Student} to add to the store
     * @throws IllegalArgumentException if {@code student} is null
     * @throws IllegalStateException    if the card cannot receive any more students
     */
    @Override
    public void receiveStudent(Student student) {
        if (student == null) throw new IllegalArgumentException("student shouldn't be null");
        if (getStudents().size() == maxStudentAmount)
            throw new IllegalStateException("This card cannot receive any more students");
        students.putIfAbsent(student.getColor(), new Stack<>());
        students.get(student.getColor()).push(student);
    }
}
