package it.polimi.ingsw.client.control.state;

import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.enums.TowerColor;

import java.util.Arrays;

/**
 * Client's representation of game's Island object.
 *
 * @author Mattia Busso
 */
public class IslandGroup {

    /**
     * The ids' of the islands inside the group.
     */
    private int[] ids;

    /**
     * The towers on the island group.
     */
    private TowerColor[] towers;

    /**
     * The students on the island group.
     */
    private PieceColor[] students;

    /**
     * The blocks on the island group.
     */
    private int blocks;

    /**
     * Returns the islands group ids.
     *
     * @return the islands group ids
     */
    public int[] getIds() {
        return ids;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if(ids != null) {
            s.append("   ");
            for (int id : ids) s.append(id);
            s.append("\n");
        }
        s.append(towers == null ? "" : "      Towers: " + Arrays.toString(towers) + "\n");
        s.append(students == null ? "" : "      Students: " + Arrays.toString(students) + "\n");
        s.append(blocks == 0 ? "" : "      Blocks: " + blocks + "\n");
        return s.toString();
    }

}
