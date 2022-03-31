package it.polimi.ingsw.server.model;

import java.util.NoSuchElementException;

/**
 * This class provides a concrete implementation of the {@link PlayerListIterator} abstract class.
 * This iterator offers a custom implementation that allows to start the iteration from a given {@code startIndex} of the list.
 * Iterating from element @ {@code startIndex} to last element @ {@code startIndex - 1 || list.size() - 1}, it conventionally follows a clockwise scan of the list.
 *
 * @author Mattia Busso
 * @see PlayerListIterator
 */
class ClockWiseIterator extends PlayerListIterator {

    /**
     * Flag that indicates if we are currently performing the first iteration.
     */
    private boolean start = true;

    /**
     * The index of the {@code Player} from which to start iterating.
     */
    private final int startIndex;

    /**
     * The index of the current {@code Player} pointed by the iterator.
     */
    private int currentIndex;

    /**
     * Custom {@code startIndex} constructor.
     * Calls the constructor of the parent class {@link PlayerListIterator} and sets the custom {@code startIndex}.
     *
     * @param list the list to iterate on
     * @param startIndex the custom {@code startIndex} for the iteration
     * @throws IllegalArgumentException if {@code list == null}
     * @throws IndexOutOfBoundsException if {@code startIndex} is out of range
     */
    ClockWiseIterator(PlayerList list, int startIndex) throws IllegalArgumentException, IndexOutOfBoundsException {
        super(list);

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
    public Player next() throws NoSuchElementException {
        Player nextPlayer;

        if(currentIndex == getList().size()) {
            currentIndex = 0;
        }

        if(currentIndex == startIndex) {
            if(start) {
                nextPlayer = getList().get(currentIndex);
                currentIndex++;
                start = false;
                return nextPlayer;
            }
            else {
                throw new NoSuchElementException("no more elements to iterate on");
            }
        }

        nextPlayer = getList().get(currentIndex);
        currentIndex++;
        return nextPlayer;

    }

    /**
     * Returns {@code true} if the iteration has more elements
     * (in other words, returns {@code true} if {@code next()} would return an element, {@code false} otherwise).
     *
     * @return {@code true} if the iteration has more elements, {@code false} otherwise.
     */
    public boolean hasNext() {
        if(currentIndex == getList().size()) {
            currentIndex = 0;
        }
        return (currentIndex != startIndex) || start;
    }

}
