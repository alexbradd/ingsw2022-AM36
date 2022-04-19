package it.polimi.ingsw.server.model.iterators;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * This class provides an iterator for a {@link List} of abstract elements {@code E}.
 * This implementation iterates through the list from an element at index {@code startIndex}
 * to an element at either index {@code startIndex - 1} or {@code list.size() - 1},
 * performing a clock-wise scan.
 * The class inherits from {@link CyclicalIterator} since it offers a single cycle version of the parent class.
 *
 * @author Mattia Busso
 * @see CyclicalIterator
 */
public class ClockWiseIterator<E> extends CyclicalIterator<E> {

    /**
     * Flag that indicates if we are currently performing the first iteration.
     */
    private boolean start = true;

    /**
     * The index from which to start iterating.
     */
    private final int startIndex;

    /**
     * Custom {@code startIndex} constructor.
     * Calls the constructor of the parent class {@link CyclicalIterator} and sets the custom {@code startIndex}.
     *
     * @param list the list to iterate on
     * @param startIndex the custom {@code startIndex} for the iteration
     * @throws IllegalArgumentException if {@code list == null} or list is empty
     * @throws IndexOutOfBoundsException if {@code startIndex} is out of range
     */
    public ClockWiseIterator(List<E> list, int startIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
        super(list, startIndex);
        this.startIndex = startIndex;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    public E next() throws NoSuchElementException {
        if((super.getCurrentIndex() == startIndex) && !start) {
            throw new NoSuchElementException("no more elements to iterate on");
        }
        if(start) {
            start = false;
        }
        return super.next();
    }

    /**
     * Returns {@code true} if the iteration has more elements
     * (in other words, returns {@code true} if {@code next()} would return an element, {@code false} otherwise).
     *
     * @return {@code true} if the iteration has more elements, {@code false} otherwise.
     */
    public boolean hasNext() {
        return (super.getCurrentIndex() != startIndex) || start;
    }

}
