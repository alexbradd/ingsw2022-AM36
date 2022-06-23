package it.polimi.ingsw.client.control.state;

import it.polimi.ingsw.enums.PieceColor;

import java.util.Arrays;

/**
 * Client's representation of game's Cloud object.
 *
 * @author Mattia Busso
 */
public class Cloud {

    /**
     * The id of the cloud.
     */
    private int id;

    /**
     * The students on the cloud.
     */
    private PieceColor[] students;

    // getters

    /**
     * Returns the students on the cloud.
     *
     * @return the students on the cloud
     */
    public PieceColor[] getStudents() {
        return students;
    }

    /**
     * Returns the id of the cloud.
     *
     * @return the id of the cloud
     */
    public int getId() {
        return id;
    }

    // stringify

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "   Id: " + id + " - " + "Students: " + Arrays.toString(students) + "\n";
    }

}
