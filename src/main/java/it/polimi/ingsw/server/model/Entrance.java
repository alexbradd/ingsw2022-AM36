package it.polimi.ingsw.server.model;

import java.util.*;

/**
 * This class models the game's entrance piece.
 *
 * @author Mattia Busso
 */
public class Entrance implements StudentMoveSource, StudentMoveDestination {

    /**
     * The player who owns the entrance.
     */
    private final Player owner;

    /**
     * Data structure containing the students present in the entrance.
     */
    private final EnumMap<PieceColor, Stack<Student>> entrance;

    /**
     * The maximum number students allowed inside the entrance.
     */
    private final int maxSize;

    /**
     * Entrance constructor.
     *
     * @param owner
     * @param maxSize
     * @throws IllegalArgumentException owner should not be null
     * @throws IllegalArgumentException maxSize should not be <= 0
     */
    Entrance(Player owner, int maxSize) throws IllegalArgumentException {
        if (owner == null) {
            throw new IllegalArgumentException("owner should not be null");
        }
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize should not be <= 0");
        }
        this.owner = owner;
        this.maxSize = maxSize;
        entrance = new EnumMap<>(PieceColor.class);
    }

    /**
     * Returns the number of players currently inside the entrance.
     *
     * @return size
     */
    int size() {
        int size = 0;
        for (Stack<Student> e : entrance.values()) {
            size += e.size();
        }
        return size;
    }

    /**
     * Adds a given student to the entrance.
     *
     * @param student
     * @throws IllegalArgumentException student should not be null
     * @throws IllegalStateException    can't add a student if the entrance is full
     * @see StudentMoveDestination
     */
    @Override
    public void receiveStudent(Student student) throws IllegalArgumentException, IllegalStateException {
        if (student == null) {
            throw new IllegalArgumentException("student should not be null");
        }
        if (size() == maxSize) {
            throw new IllegalStateException("can't add student to full entrance");
        }
        entrance.computeIfAbsent(student.getColor(), k -> new Stack<>());
        entrance.get(student.getColor()).add(student);
    }

    /**
     * Pops a student of a given color from the entrance and returns it.
     *
     * @param color
     * @return student
     * @throws IllegalStateException can't use this method if there are no students of a given color in the entrance
     * @see StudentMoveSource
     */
    @Override
    public Student sendStudent(PieceColor color) throws IllegalStateException {
        if (entrance.get(color) == null) {
            throw new IllegalStateException("can't get student of a given color not present in the entrance");
        }
        return entrance.get(color).pop();
    }

    /**
     * @return boolean
     * @see StudentMoveSource
     * @see StudentMoveDestination
     */
    @Override
    public boolean requiresProfessorAssignment() {
        return false;
    }

    /**
     * Owner getter.
     *
     * @return owner
     */
    Player getOwner() {
        return owner;
    }

    /**
     * maxSize getter.
     *
     * @return maxSize
     */
    int getMaxSize() {
        return maxSize;
    }

}
