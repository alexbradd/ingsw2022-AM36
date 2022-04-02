package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

import java.util.*;

/**
 * Represents an abstract character card. Each card has one "character", a cost that goes up by 1 after the first
 * use and two hooks into respectively the {@link PreparePhase} and {@link ActionPhase} of the game. This base class has
 * only default implementations that either do nothing, or throw an exception. Please see all subclasses for different
 * card effects.
 *
 * @author Alexandru Gabriel Bradatan
 * @see ActionPhase
 * @see PreparePhase
 */
public abstract class Character implements BidirectionalStudentMove {
    /**
     * This card's character. Represented as a {@link Characters} enum.
     */
    private final Characters character;

    /**
     * The base cost of the card.
     */
    private final int initialCost;

    /**
     * Indicates if a card has been used at least once.
     */
    private boolean used;

    /**
     * Base constructor. Sets up only the card's initial cost and character
     *
     * @param character   this card's character
     * @param initialCost initial cost of the card
     * @throws IllegalArgumentException if {@code initialCost} is less than zero
     */
    Character(Characters character, int initialCost) {
        if (initialCost < 0) throw new IllegalArgumentException("initialCost should be >= 0");
        this.character = character;
        this.initialCost = initialCost;
        used = false;
    }

    /**
     * Getter for this card's character
     *
     * @return this card's character
     */
    Characters getCharacter() {
        return character;
    }

    /**
     * Getter for this card's actual cost. This means that this will return the card's initial cost if this card hasn't
     * been used before and the initial cos +1 otherwise.
     *
     * @return this card's actual cost
     */
    int getCost() {
        return used ? initialCost + 1 : initialCost;
    }

    /**
     * Default implementation for the {@link PreparePhase} hook. This implementation only checks for nullity, so if
     * subclasses want to not rewrite it themselves, they can call this implementation.
     *
     * @param phase the {@link PreparePhase} the card's hook has been called from
     * @throws IllegalArgumentException if {@code phase} is null
     */
    void doPrepare(PreparePhase phase) {
        if (phase == null) throw new IllegalArgumentException("phase shouldn't be null");
    }

    /**
     * Default implementation for the card's effect. This implementation only marks the card as used and does nothing.
     * All subclasses should call this implementation if they are overriding this method. The layout of the
     * {@code parameters} array is detailed in each subclass.
     *
     * @param phase      the {@link ActionPhase} the card's effect has been called from
     * @param parameters an array of strings configuring the effect
     * @throws IllegalArgumentException           if {@code phase} is null
     * @throws InvalidCharacterParameterException if any of the strings in {@code parameters} is formatted
     *                                            incorrectly
     */
    void doEffect(ActionPhase phase, String[] parameters) throws InvalidCharacterParameterException {
        if (phase == null) throw new IllegalArgumentException("phase shouldn't be null");
        used = true;
    }

    /**
     * Returns the students currently placed on the card. This implementation will always return an empty set since
     * sending/receiving {@link Student}s always throws an exception.
     *
     * @return an empty set of {@link Student}
     */
    Set<Student> getStudents() {
        return new HashSet<>();
    }

    /**
     * Remove and return a {@link Student} of the given color from the store. This implementation of the method will
     * throw a {@link UnsupportedOperationException}.
     *
     * @param color {@link Student}'s color to send
     * @return a {@link Student} from the store
     * @throws UnsupportedOperationException always
     */
    @Override
    public Student sendStudent(PieceColor color) {
        throw new UnsupportedOperationException("this card cannot send students");
    }

    /**
     * Adds the given {@link Student} to the internal store. This implementation of the method will throw a
     * {@link UnsupportedOperationException}.
     *
     * @param student the {@link Student} to add to the store
     * @throws IllegalArgumentException      if {@code student} is null
     * @throws UnsupportedOperationException always
     */
    @Override
    public void receiveStudent(Student student) {
        if (student == null) throw new IllegalArgumentException("student shouldn't be null");
        throw new UnsupportedOperationException("this card cannot receive students");
    }

    /**
     * Returns true if sending or receiving a {@link Student} modifies the {@link Professor} assignments. For cards,
     * this will always return false
     *
     * @return false
     */
    @Override
    public boolean requiresProfessorAssignment() {
        return false;
    }

    /**
     * Returns true if this card contains this particular {@link BlockCard} in its store. This implementation will
     * throw a {@link UnsupportedOperationException}.
     *
     * @return true if this card contains this particular {@link BlockCard} in its store
     * @throws IllegalArgumentException      if {@code block} is null
     * @throws UnsupportedOperationException always
     */
    boolean containsBlock(BlockCard block) {
        if (block == null) throw new IllegalArgumentException("block shouldn't be null");
        throw new UnsupportedOperationException("this card doesn't receive blocks");
    }

    /**
     * Returns the number of {@link BlockCard} available on this card. This implementation always returns 0 since
     * block related methods always throw an exception.
     *
     * @return the number of {@link BlockCard} available on this card
     */
    int getNumOfBlocks() {
        return 0;
    }

    /**
     * Adds the given {@link BlockCard} to the internal store. This implementation of the method throws a
     * {@link UnsupportedOperationException}.
     *
     * @param block the {@link BlockCard} to add to the store
     * @throws IllegalArgumentException      if {@code block} is null
     * @throws UnsupportedOperationException always
     */
    void pushBlock(BlockCard block) {
        if (block == null) throw new IllegalArgumentException("block shouldn't be null");
        throw new UnsupportedOperationException("this card doesn't receive blocks");
    }

    /**
     * Pops a {@link BlockCard} from the internal store and returns it. This implementation of the method throws a
     * {@link UnsupportedOperationException}.
     *
     * @return a {@link BlockCard}
     * @throws UnsupportedOperationException always
     */
    BlockCard popBlock() {
        throw new UnsupportedOperationException("this card doesn't produce blocks");
    }

    /**
     * Static utility used for parsing an integer from an array of strings at the given position and converting
     * potential exceptions into {@link InvalidCharacterParameterException}.
     *
     * @param strings the array of strings
     * @param pos     ths position of the integer
     * @return the integer
     * @throws InvalidCharacterParameterException if the integer is not formatted correctly
     */
    static int parseInteger(String[] strings, int pos) throws InvalidCharacterParameterException {
        String s = strings[pos];
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            String msg = InvalidCharacterParameterException.message(pos, s + "not a number");
            throw new InvalidCharacterParameterException(msg, e);
        }
    }

    /**
     * Static utility used for parsing an {@link PieceColor} literal from an array of strings at the given position and
     * converting potential exceptions into {@link InvalidCharacterParameterException}.
     *
     * @param strings the array of strings
     * @param pos     ths position of the literal
     * @return the {@link PieceColor}
     * @throws InvalidCharacterParameterException if the literal is not formatted correctly
     */
    static PieceColor parseColor(String[] strings, int pos) throws InvalidCharacterParameterException {
        String s = strings[pos];
        try {
            return PieceColor.valueOf(s);
        } catch (IllegalArgumentException e) {
            String msg = InvalidCharacterParameterException.message(pos, s + "not a PieceColor");
            throw new InvalidCharacterParameterException(msg, e);
        }
    }

    /**
     * Static utility for retrieving the {@link Island} at the given index if possible. If it is not possible, an
     * exception will be thrown.
     *
     * @param phase       the phase from which to retrieve the island
     * @param islandIndex the index of the island
     * @return the {@link Island}
     * @throws InvalidCharacterParameterException if retrieving the {@link Island} was not possible
     */
    static Island getIsland(ActionPhase phase, int islandIndex) throws InvalidCharacterParameterException {
        try {
            return phase.getIsland(islandIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidCharacterParameterException("island index is out of bounds", e);
        }
    }
}
