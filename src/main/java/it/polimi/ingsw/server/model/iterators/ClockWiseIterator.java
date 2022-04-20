package it.polimi.ingsw.server.model.iterators;

import it.polimi.ingsw.server.model.Board;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

// TODO docs

/**
 * This class provides an implementation of the Iterator<{@link Board}> interface.
 * This iterator offers a custom implementation that allows to start the iteration from a given {@code startIndex} of the list.
 * Iterating from element @ {@code startIndex} to last element @ {@code startIndex - 1 || list.size() - 1}, it conventionally follows a clockwise scan of the list.
 *
 * @author Mattia Busso
 * @see Board
 */
public class ClockWiseIterator implements Iterator<Board> {

    /**
     * The list of boards to iterate on.
     */
    private final List<Board> list;

    /**
     * Flag that indicates if we are currently performing the first iteration.
     */
    private boolean start = true;

    /**
     * The index of the {@code Board} from which to start iterating.
     */
    private final int startIndex;

    /**
     * The index of the current {@code Board} pointed by the iterator.
     */
    private int currentIndex;

    /**
     * Custom {@code startIndex} constructor.
     * Sets the list of boards to the one passed via parameter and sets the custom {@code startIndex}.
     *
     * @param list the list to iterate on
     * @param startIndex the custom {@code startIndex} for the iteration
     * @throws IllegalArgumentException if {@code list == null}
     * @throws IndexOutOfBoundsException if {@code startIndex} is out of range
     */
    public ClockWiseIterator(List<Board> list, int startIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (list == null) throw new IllegalArgumentException("list must not be null.");
        this.list = list;

        if(list.isEmpty()) {
            throw new IllegalArgumentException("list shouldn't be empty");
        }

        if(startIndex < 0 || startIndex >= list.size()) {
            throw new IndexOutOfBoundsException("invalid startIndex");
        }
        this.startIndex = startIndex;
        currentIndex = startIndex;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements or if the list is empty
     */
    public Board next() throws NoSuchElementException {
        Board nextBoard;

        if(currentIndex == list.size()) {
            currentIndex = 0;
        }

        if(currentIndex == startIndex) {
            if(start) {
                nextBoard = list.get(currentIndex);
                currentIndex++;
                start = false;
                return nextBoard;
            }
            else {
                throw new NoSuchElementException("no more elements to iterate on");
            }
        }

        nextBoard = list.get(currentIndex);
        currentIndex++;
        return nextBoard;

    }

    /**
     * Returns {@code true} if the iteration has more elements
     * (in other words, returns {@code true} if {@code next()} would return an element, {@code false} otherwise).
     *
     * @return {@code true} if the iteration has more elements, {@code false} otherwise.
     */
    public boolean hasNext() {
        if(currentIndex == list.size()) {
            currentIndex = 0;
        }
        return (currentIndex != startIndex) || start;
    }

}
