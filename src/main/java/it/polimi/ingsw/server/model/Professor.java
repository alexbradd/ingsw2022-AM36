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
    private Player owner;

    /**
     * Professor constructor.
     * Initially no player owns the professor, so the reference is left set to null.
     *
     * @param color the color of the professor
     */
    Professor(PieceColor color) {
        this.color = color;
        this.owner = null;
    }

    /**
     * Assigns new owner to professor.
     *
     * @param owner the player that owns the professor
     * @throws IllegalArgumentException newly assigned owner should not be null
     */
    void assign(Player owner) throws IllegalArgumentException {
        if (owner == null) {
            throw new IllegalArgumentException("newly assigned owner should not be null");
        }
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
