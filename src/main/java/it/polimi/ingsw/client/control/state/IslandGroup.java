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
        s.append(towers == null ? "" : "      Towers: " + countTowersColor() + "\n");
        s.append(students == null ? "" : "      Students: " + countStudentsColor() + "\n");
        s.append(blocks == 0 ? "" : "      Blocks: " + blocks + "\n");
        return s.toString();
    }

    /**
     * Helper method that returns a color-frequency custom formatted string for the students containers.
     *
     * @return the custom formatted string
     */
    private String countStudentsColor() {
        StringBuilder s = new StringBuilder();

        for(PieceColor color : PieceColor.values()) {
            int count = 0;
            for (PieceColor student : students) {
                if (student.equals(color)) count++;
            }
            s.append(count == 0 ? "" : " " + count + "x" + color);
        }

        return s.toString();
    }

    /**
     * Helper method that returns a color-frequency custom formatted string for the towers.
     *
     * @return the custom formatted string
     */
    private String countTowersColor() {
        StringBuilder s = new StringBuilder();

        for(TowerColor color : TowerColor.values()) {
            int count = 0;
            for (TowerColor tower : towers) {
                if (tower.equals(color)) count++;
            }
            s.append(count == 0 ? "" : " " + count + "x" + color);
        }

        return s.toString();
    }

}
