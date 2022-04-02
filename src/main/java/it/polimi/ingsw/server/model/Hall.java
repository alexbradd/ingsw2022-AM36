package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;

import java.util.*;

/**
 * This class models the game's hall.
 *
 * @author Mattia Busso
 */
public class Hall implements StudentMoveSource, StudentMoveDestination {

    /**
     * The player who owns the hall.
     */
    private final Player owner;

    /**
     * Data structure that contains the students present in the hall.
     */
    private final EnumMap<PieceColor, Stack<Student>> hall;

    /**
     * Hall constructor.
     *
     * @param owner
     * @throws IllegalArgumentException owner should not be null
     */
    Hall(Player owner) throws IllegalArgumentException {
        if (owner == null) {
            throw new NullPointerException("owner should not be null");
        }
        this.owner = owner;
        hall = new EnumMap<>(PieceColor.class);
    }

    /**
     * Adds a given student to the hall.
     *
     * @param student
     * @throws IllegalArgumentException student should not be null
     * @see StudentMoveDestination
     */
    @Override
    public void receiveStudent(Student student) throws IllegalArgumentException {
        if (student == null) {
            throw new NullPointerException("student should not be null");
        }
        hall.computeIfAbsent(student.getColor(), k -> new Stack<>());
        hall.get(student.getColor()).add(student);
    }

    /**
     * Pops a student of a given color from the hall and returns it.
     *
     * @param color
     * @return student
     * @throws IllegalStateException can't use this method if the hall of the given color is currently empty.
     * @see StudentMoveSource
     */
    @Override
    public Student sendStudent(PieceColor color) throws IllegalStateException {
        if (sizeOfColor(color) == 0) {
            throw new IllegalStateException("can't get student from empty hall collection");
        }
        return hall.get(color).pop();
    }

    /**
     * @return boolean
     * @see StudentMoveSource
     * @see StudentMoveDestination
     */
    @Override
    public boolean requiresProfessorAssignment() {
        return true;
    }

    /**
     * Returns the size of the hall of the given color.
     *
     * @param color
     * @return size
     */
    int sizeOfColor(PieceColor color) {
        return (hall.get(color) == null ? 0 : hall.get(color).size());
    }

    /**
     * Owner getter.
     *
     * @return owner
     */
    Player getOwner() {
        return owner;
    }

}
