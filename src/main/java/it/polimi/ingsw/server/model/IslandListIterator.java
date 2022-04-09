package it.polimi.ingsw.server.model;

import java.util.Iterator;

/**
 * A class for iterating in cyclical fashion over the groups in a {@link IslandList}. Ungrouped tiles are treated as
 * groups of only one island.
 *
 * @author Alexandru Gabriel Bradatan
 * @see IslandList
 */
class IslandListIterator implements Iterator<Island> {
    /**
     * List that the object iterates on.
     */
    private final IslandList list;

    /**
     * Index at which iteration is at.
     */
    private int currentIndex;

    /**
     * Group at which iteration is at.
     */
    private Island currentGroup;

    /**
     * Create a new iterator on the given {@link IslandList}.
     *
     * @param list the list to iterate on
     * @throws IllegalArgumentException if {@code list} is null
     */
    IslandListIterator(IslandList list) {
        if (list == null) throw new IllegalArgumentException("list shouldn't be null");
        this.list = list;
        currentIndex = 0;
        currentGroup = null;
    }

    /**
     * Create a new iterator on the given {@link IslandList} that starts iteration on the given index.
     *
     * @param list             the list to iterate on
     * @param startingPosition the position from which to start iteration
     * @throws IllegalArgumentException if {@code list} is null
     * @throws IllegalArgumentException if {@code startingPosition} is out of bounds
     */
    IslandListIterator(IslandList list, int startingPosition) {
        if (list == null) throw new IllegalArgumentException("list shouldn't be null");
        if (startingPosition < 0 || startingPosition > list.size())
            throw new IllegalArgumentException("startingPosition " + startingPosition + "out of bounds");
        this.list = list;
        currentIndex = startingPosition;
        currentGroup = null;
    }

    /**
     * Create a new iterator on the given {@link IslandList} that starts iteration at the given {@link Island}.
     *
     * @param list  the list to iterate on
     * @param start the {@link Island} from which to start iteration
     * @throws IllegalArgumentException if {@code list} is null
     * @throws IllegalArgumentException if {@code start} is null or not in {@code list}
     */
    IslandListIterator(IslandList list, Island start) {
        if (list == null) throw new IllegalArgumentException("list shouldn't be null");
        if (start == null) throw new IllegalArgumentException("start shouldn't be null");
        if (!list.contains(start))
            throw new IllegalArgumentException("cannot start from an island that is not in the list");
        this.list = list;
        currentIndex = list.indexOf(start);
        currentGroup = start;
    }

    /**
     * Returns true if this list iterator has more elements when traversing the list in the forward direction. Since
     * this is cyclical iteration, it will always return true.
     *
     * @return true
     */
    @Override
    public boolean hasNext() {
        return true;
    }

    /**
     * Returns the next group in the list and advances the cursor position. This method may be called repeatedly to
     * iterate through the list.
     *
     * @return the next group in the list
     */
    @Override
    public Island next() {
        Island current = list.get(currentIndex);
        currentIndex++;
        if (currentIndex >= list.size())
            currentIndex = 0;
        if (currentGroup != null && current.isRelatedTo(currentGroup))
            return next();
        currentGroup = current;
        return current;
    }

    /**
     * Returns a string representation of this iterator.
     *
     * @return a string representation of this iterator
     */
    @Override
    public String toString() {
        return "IslandListIterator{" +
                "list=" + list +
                ", currentIndex=" + currentIndex +
                '}';
    }
}
