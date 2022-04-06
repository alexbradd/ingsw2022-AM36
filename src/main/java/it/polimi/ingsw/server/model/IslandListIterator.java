package it.polimi.ingsw.server.model;

import java.util.Iterator;
import java.util.List;

/**
 * A class for iterating in cyclical fashion over a list of {@link Island}.
 *
 * @author Alexandru Gabriel Bradatan
 */
class IslandListIterator implements Iterator<Island> {
    /**
     * List that the object iterates on.
     */
    private final List<Island> list;

    /**
     * Index at which iteration is at.
     */
    private int currentIndex;

    /**
     * Create a new iterator on the given list.
     *
     * @param list the list to iterate on
     * @throws IllegalArgumentException if {@code list} is null
     */
    IslandListIterator(List<Island> list) {
        if (list == null) throw new IllegalArgumentException("list shouldn't be null");
        this.list = list;
        currentIndex = 0;
    }

    /**
     * Create a new iterator on the given list that starts iteration on the given index. The next call to {@code next()}
     * will return the item after the one with the given index.
     *
     * @param list             the list to iterate on
     * @param startingPosition the position from which to start iteration
     * @throws IllegalArgumentException if {@code list} is null
     * @throws IllegalArgumentException if {@code startingPosition} is out of bounds
     */
    IslandListIterator(List<Island> list, int startingPosition) {
        if (list == null) throw new IllegalArgumentException("list shouldn't be null");
        if (startingPosition < 0 || startingPosition > list.size())
            throw new IllegalArgumentException("startingPosition " + startingPosition + "out of bounds");
        this.list = list;
        currentIndex = startingPosition + 1;
    }

    /**
     * Create a new iterator on the given list that starts iteration at the given {@link Island}. The next call to
     * {@code next()} will return the item after the given one.
     *
     * @param list  the list to iterate on
     * @param start the {@link T} from which to start iteration
     * @throws IllegalArgumentException if {@code list} is null
     * @throws IllegalArgumentException if {@code start} is null or not in {@code list}
     */
    IslandListIterator(List<Island> list, Island start) {
        if (list == null) throw new IllegalArgumentException("list shouldn't be null");
        if (start == null) throw new IllegalArgumentException("start shouldn't be null");
        if (!list.contains(start))
            throw new IllegalArgumentException("cannot start from an island that is not in the list");
        this.list = list;
        currentIndex = list.indexOf(start) + 1;
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
        if (currentIndex >= list.size())
            currentIndex = 0;
        Island current = list.get(currentIndex);
        currentIndex++;
        if (currentIndex >= list.size())
            currentIndex = 0;
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
