package it.polimi.ingsw.server.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.enums.PieceColor;

import java.util.Objects;
import java.util.Optional;

/**
 * This class models the game's Professors.
 *
 * @author Mattia Busso
 */
class Professor implements Jsonable {

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
     *
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Professor professor = (Professor) o;
        if (owner == null)
            return color == professor.color && professor.owner == null;
        return color == professor.color && owner.equals(professor.owner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(color, owner);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement toJson() {
        JsonObject ret = new JsonObject();
        ret.addProperty("color", getColor().toString());
        getOwner().ifPresent(p -> ret.add("owner", p.toJson()));
        return ret;
    }
}
