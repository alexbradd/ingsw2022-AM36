package it.polimi.ingsw.server.model;

/**
 * This class models the game's Student pieces.
 *
 * @author Mattia Busso
 */
class Student {

    /**
     * Color of the student piece.
     */
    private final PieceColor color;

    /**
     * The corresponding student's professor.
     */
    private final Professor professor;

    /**
     * Student constructor.
     *
     * @param professor
     * @throws IllegalArgumentException professor should not be null
     */
    Student(Professor professor) throws IllegalArgumentException {
        if(professor == null) {
            throw new IllegalArgumentException("professor should not be null");
        }

        this.professor = professor;
        this.color = professor.getColor();
    }

    /**
     * Professor getter.
     *
     * @return professor
     */
    Professor getProfessor() {
        return this.professor;
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
