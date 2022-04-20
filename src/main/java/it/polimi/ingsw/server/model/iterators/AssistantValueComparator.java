package it.polimi.ingsw.server.model.iterators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Player;

/**
 * This class provides a {@link Comparator} for a {@link List} of {@link Player},
 * based on the player's {@code lastPlayedAssistant.orderValue}.
 * The player with the lowest {@code lastPlayedAssistant.orderValue} comes before the one with the highest,
 * in case of ties the {@code startIndex} is used.
 *
 * @author Mattia Busso
 * @see AssistantValueIterator
 */
class AssistantValueComparator implements Comparator<Board> {

    /**
     * A copy of the list in which compared boards are present.
     */
    private final List<Board> list;

    /**
     * The startIndex of the boards inside the list.
     */
    private final int startIndex;

    /**
     * Basic constructor.
     *
     * @param list the list on which boards to compare are present
     * @param startIndex the startIndex of the list used to break ties
     */
    AssistantValueComparator(List<Board> list, int startIndex) {
        this.list = new ArrayList<>(list);
        this.startIndex = startIndex;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if one of the boards to compare does not have a {@code lastPlayedAssistant}
     */
    @Override
    public int compare(Board o1, Board o2) throws IllegalStateException {
        int assistantValueOne = o1.getLastPlayedAssistant().orElseThrow(IllegalStateException::new).getOrderValue();
        int assistantValueTwo = o2.getLastPlayedAssistant().orElseThrow(IllegalStateException::new).getOrderValue();

        if(assistantValueOne < assistantValueTwo) return -1;

        if(assistantValueTwo < assistantValueOne) return +1;

        if(isBefore(list.indexOf(o1), list.indexOf(o2))) {
            return -1;
        }
        else {
            return +1;
        }

    }

    /**
     * Returns {@code true} if {@code i} is the index of a {@code Board} inside the list that comes before
     * the {@code Board} index by {@code j}.
     * Used in case of boards with the same {@code lastPlayedAssistant.orderValue}
     *
     * @param i the index of the first {@code Board}
     * @param j the index of the second {@code Board}
     * @return {@code true} if {@code i} indexes a board that comes before the board indexed by currentIndex, {@code false} otherwise
     */
    private boolean isBefore(int i, int j) {
        return ((i < j)) || (j < startIndex && i > startIndex) || (i == startIndex);
    }


}
