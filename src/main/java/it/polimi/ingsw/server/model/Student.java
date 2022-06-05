package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.PieceColor;

/**
 * This class models the game's Student pieces.
 *
 * @author Mattia Busso, Leonardo Bianconi
 */
public final class Student {

    /**
     * Color of the student piece.
     */
    private final PieceColor color;

    /**
     * Student constructor.
     *
     * @throws IllegalArgumentException color should not be null
     */
    Student(PieceColor color) throws IllegalArgumentException {
        if(color == null) {
            throw new IllegalArgumentException("professor should not be null");
        }

        this.color = color;
    }

    /**
     * Color getter.
     *
     * @return color
     */
    PieceColor getColor() {
        return this.color;
    }
}
