package it.polimi.ingsw.client.control.state;

import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.PieceColor;

import java.util.Arrays;

/**
 * Client representation of game's Character object.
 *
 * @author Mattia Busso
 */
public class Character {

    /**
     * The character's type.
     */
    private CharacterType type;

    /**
     * The students on the character card.
     */
    private PieceColor[] students;

    /**
     * The cost of the character.
     */
    private int price;

    // getters

    /**
     * Returns the type of the character
     *
     * @return the type of the character
     */
    public CharacterType getType() {
        return type;
    }

    /**
     * Returns the price of the character
     *
     * @return the price of the character
     */
    public int getPrice() {
        return price;
    }

    /**
     * Returns the students on the character card.
     *
     * @return the students on the character card
     */
    public PieceColor[] getStudents() {
        return students;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "   " + type + ":\n" + "      cost: " + price + "\n" +
                (students == null ? "" : "      students: " + Arrays.toString(students) + "\n");
    }

}
