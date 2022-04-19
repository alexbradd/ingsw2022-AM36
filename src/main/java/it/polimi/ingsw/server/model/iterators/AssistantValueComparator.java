package it.polimi.ingsw.server.model.iterators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
class AssistantValueComparator implements Comparator<Player> {

    /**
     * A copy of the list in which compared players are present.
     */
    private final List<Player> list;

    /**
     * The startIndex of the players inside the list.
     */
    private final int startIndex;

    /**
     * Basic constructor.
     *
     * @param list the list on which players to compare are present
     * @param startIndex the startIndex of the list used to break ties
     */
    AssistantValueComparator(List<Player> list, int startIndex) {
        this.list = new ArrayList<>(list);
        this.startIndex = startIndex;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalStateException if one of the players to compare does not have a {@code lastPlayedAssistant}
     */
    @Override
    public int compare(Player o1, Player o2) throws IllegalStateException {
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
     * Returns {@code true} if {@code i} is the index of a {@code Player} inside the list that comes before
     * the {@code Player} index by {@code j}.
     * Used in case of players with the same {@code lastPlayedAssistant.orderValue}
     *
     * @param i the index of the {@code Player} to check the order of
     * @return {@code true} if {@code i} indexes a player that comes before the player indexed by currentIndex, {@code false} otherwise
     */
    private boolean isBefore(int i, int j) {
        return ((i < j)) || (j < startIndex && i > startIndex) || (i == startIndex);
    }


}
