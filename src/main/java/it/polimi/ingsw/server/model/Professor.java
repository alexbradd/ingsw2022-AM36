package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;

import java.util.Optional;

/**
 * This class models the game's Professors.
 *
 * @author Mattia Busso
 */
public class Professor {

    /**
     * The professor's piece color.
     */
    private final PieceColor color;

    /**
     * Reference to the player owner of the professor.
     */
    private final Player owner;

    /**
     * Professor constructor.
     * Initially no player owns the professor, so the reference is left set to null.
     *
     * @param color the color of the professor
     * @throws IllegalArgumentException if {@code color} is null
     */
    Professor(PieceColor color) {
        if (color == null) throw new IllegalArgumentException("color shouldn't be null");
        this.color = color;
        this.owner = null;
    }

    /**
     * Create a new Professor with the specified color and owner
     * @param color the color of the Professor
     * @param owner the owner of the Professor
     * @throws IllegalArgumentException if any parameter is null
     */
    Professor(PieceColor color, Player owner) {
        if (color == null) throw new IllegalArgumentException("color shouldn't be null");
        if (owner == null) throw new IllegalArgumentException("owner shouldn't be null");
        this.color = color;
        this.owner = owner;
    }

    /**
     * Owner getter.
     * Since a newly created professor doesn't have an owner, the method returns an Optional<Player>.
     *
     * @return owner
     */
    Optional<Player> getOwner() {
        return Optional.ofNullable(owner);
    }

    /**
     * Piece color getter.
     *
     * @return color
     */
    PieceColor getColor() {
        return color;
    }
}
