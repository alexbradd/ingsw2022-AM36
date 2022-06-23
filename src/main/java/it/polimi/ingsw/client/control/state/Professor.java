package it.polimi.ingsw.client.control.state;

import it.polimi.ingsw.enums.PieceColor;

import java.util.Objects;

/**
 * Client's representation of game's Professor object.
 *
 * @author Mattia Busso
 */
public class Professor {

    /**
     * The professor's color.
     */
    private PieceColor color;

    /**
     * The player who owns the professor.
     */
    private String owner;

    /**
     * Returns the color of the professor.
     *
     * @return the color of the professor
     */
    public PieceColor getColor() {
        return color;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "   " + color + " professor, owned by " +
                (owner == null || Objects.equals(owner, "") ? "no-one" : owner) +
                "\n";
    }

}
