package it.polimi.ingsw.server.model.iterators;

import java.util.Iterator;
import java.util.List;

/**
 * This class provides an iterator for a {@link List} of abstract elements {@code E}.
 * This implementation iterates through the list from a {@code startIndex},
 * performing a cyclical and clock-wise scan.
 *
 * @author Mattia Busso
 */
public class CyclicalIterator<E> implements Iterator<E> {

    /**
     * The list to iterate on.
     */
    private final List<E> list;

    /**
     * The current index of the iteration.
     */
    private int currentIndex;

    /**
     * Basic constructor.
     *
     * @param list the list to iterate on
     * @throws IllegalArgumentException if {@code list == null} or {@code list} is empty
     */
    public CyclicalIterator(List<E> list) {
        if(list == null) {
            throw new IllegalArgumentException("list shouldn't be null");
        }
        if(list.isEmpty()) {
            throw new IllegalArgumentException("list shouldn't be empty");
        }
        this.list = list;
    }

    /**
     * Custom {@code startIndex} constructor.
     *
     * @param list the list to iterate on
     * @param startIndex the custom {@code startIndex} for the iteration
     * @throws IllegalArgumentException if {@code list  == null} or {@code list} is empty
     * @throws IndexOutOfBoundsException if {@code startIndex} is out of range
     */
    public CyclicalIterator(List<E> list, int startIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
        this(list);
        if(startIndex < 0 || startIndex >= list.size()) {
            throw new IndexOutOfBoundsException("invalid startIndex");
        }
        this.currentIndex = startIndex;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     */
    @Override
    public E next() {
        E nextEntity = list.get(currentIndex);
        if(currentIndex + 1 == list.size()) {
            currentIndex = 0;
        }
        else {
            currentIndex++;
        }
        return nextEntity;
    }

    /**
     * Returns {@code true} because the iteration is cyclical, so it always has more elements.
     *
     * @return {@code true}
     */
    @Override
    public boolean hasNext() {
        return true;
    }

    /**
     * Returns the current index of the iteration.
     *
     * @return the current index of the iteration
     */
    protected int getCurrentIndex() {
        return currentIndex;
    }

}
