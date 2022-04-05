package it.polimi.ingsw.server.model;

import java.util.Comparator;
import java.util.List;

public class PlayerWinConditionsComparator implements Comparator {

    private final List<Professor> professorList;

    public PlayerWinConditionsComparator(List<Professor> professorList) {
        this.professorList = professorList;
    }

    /**
     * A method that compares two players by their current eligibility for being a winner of the game. (See game rules)
     *
     * @param o1 the first player to compare
     * @param o2 the second player to compare
     * @return the integer value 1 if o1 is more eligible than o2, -1 if o2 is more eligible than o1 and 0 if the two
     * players are considered to be in a draw situation
     */
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null || o2 == null)
            throw new NullPointerException();
        Player p1 = (Player) o1,
                p2 = (Player) o2;

        if (p1.getNumOfTowers() == p2.getNumOfTowers())
            return getNumProfessors(p1) - getNumProfessors(p2);

        return p1.getNumOfTowers() - p2.getNumOfTowers();
    }

    /**
     * A helper method that calculates the number of controlled professor by a given player.
     *
     * @param player the player of which we want to calculate the number of controlled professors
     * @return the number of controlled professors
     */
    private int getNumProfessors(Player player) {
        int professorCount = 0;
        for (Professor professor : professorList) {
            if (professor.getPlayer().equals(player))
                professorCount++;
        }
        return professorCount;
    }

}
