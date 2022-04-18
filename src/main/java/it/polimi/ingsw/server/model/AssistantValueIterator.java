package it.polimi.ingsw.server.model;

import java.util.*;

/**
 * This class is an implementation of the Iterator<{@link Board}> interface.
 * This implementation iterates on the list by ascending order of the mapped list's {@link Player} -
 * last played {@link Assistant} {@code orderValue} attribute.
 * For more information about the mapped values consult the {@link Player} and {@link Assistant} documentations.
 *
 * @author Mattia Busso
 * @see Board
 */
class AssistantValueIterator implements Iterator<Board> {

    private final List<Board> list;

    /**
     * The starting index used to break ties in case of same-value assistants
     */
    private final int startIndex;

    /**
     * The index of the current {@code Board} pointed by the iterator.
     */
    private int currentIndex;

    /**
     * The value of the {@code lastPlayed} of the current {@code Board} pointed by the iterator.
     */
    private int currMin;

    /**
     * The index of the first {@link Board} to be returned by the iterator.
     */
    private int firstPlayedIndex;

    /**
     * A set of boards the iterator hasn't returned yet.
     */
    private final Set<Board> unReturned;

    /**
     * Flag that indicates if we are currently performing the first iteration.
     */
    private boolean start = true;

    /**
     * The upper bound for the {@code currMin} value.
     */
    private final int MIN_UPPER_BOUND = 11;

    /**
     * Custom {@code startIndex} constructor.
     * Sets the {@link #list} attribute and sets the custom {@code startIndex},
     * used to break ties in case of {@code Players} with the same {@code lastPlayedAssistant} value.
     *
     * @param list       the list to iterate on
     * @param startIndex the custom {@code startIndex} for the iteration
     * @throws IllegalArgumentException  if {@code list == null} or {@code list.size() == 0} (list is empty)
     * @throws IndexOutOfBoundsException if {@code startIndex} is out of range
     * @throws IllegalStateException     if a {@code Board} inside the {@code List<Board>} has no {@code lastPlayedAssistant}
     */
    AssistantValueIterator(List<Board> list, int startIndex) throws IllegalArgumentException, IllegalStateException, IndexOutOfBoundsException {
        if (list == null) throw new IllegalArgumentException("list must not be null.");
        this.list = list;
        this.startIndex = startIndex;
        this.firstPlayedIndex = -1;
        unReturned = new HashSet<>();

        if (list.isEmpty()) {
            throw new IllegalArgumentException("list shouldn't be empty");
        }

        if (startIndex < 0 || startIndex >= list.size()) {
            throw new IndexOutOfBoundsException("invalid startIndex");
        }

        unReturned.addAll(list);
        updateCurrentValues();
        unReturned.remove(list.get(currentIndex));

    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements or if the list is empty
     * @throws IllegalStateException  if a {@code Board} inside the {@code List<Board>} has no {@code lastPlayedAssistant}
     */
    public Board next() throws NoSuchElementException, IllegalStateException {
        Board nextBoard = list.get(currentIndex);

        if (start) {
            firstPlayedIndex = currentIndex;
            start = false;
        }

        if (currMin == MIN_UPPER_BOUND) {
            throw new NoSuchElementException("no more elements to iterate on");
        }

        updateCurrentValues();

        if (currMin != MIN_UPPER_BOUND) {
            unReturned.remove(list.get(currentIndex));
        }

        return nextBoard;

    }

    /**
     * Returns {@code true} if the iteration has more elements
     * (in other words, returns {@code true} if {@code next()} would return an element, {@code false} otherwise).
     *
     * @return {@code true} if the iteration has more elements, {@code false} otherwise.
     */
    public boolean hasNext() {
        return currMin != MIN_UPPER_BOUND;
    }

    /**
     * Returns the index of the first {@link Board} to be returned by the iterator.
     *
     * @return the index of the first board returned by {@code next()}
     * @throws IllegalStateException if no board has been returned yet
     */
    int getFirstPlayedIndex() {
        if (firstPlayedIndex == -1) {
            throw new IllegalStateException("no player has been returned yet");
        }
        return firstPlayedIndex;
    }

    /**
     * Returns {@code true} if {@code i} is the index of a {@code Board} inside the list that comes before
     * the {@code Board} index by {@code currentIndex}.
     *
     * @param i the index of the {@code Board} to check the order of
     * @return {@code true} if {@code i} indexes a player that comes before the player indexed by currentIndex, {@code false} otherwise
     */
    private boolean isBeforeCurrent(int i) {
        return ((i < currentIndex)) || (currentIndex < startIndex && i > startIndex) || (i == startIndex);
    }

    /**
     * Private method that updates {@code currentIndex} and {@code currMin} after one iteration.
     *
     * @throws IllegalStateException if a {@code Board} inside the {@code List<Board>} has no {@code lastPlayedAssistant}
     */
    private void updateCurrentValues() throws IllegalStateException {
        currMin = MIN_UPPER_BOUND;
        for (int i = 0; i < list.size(); i++) {
            if (unReturned.contains(list.get(i))) {
                int currAssistantValue = list.get(i).getLastPlayedAssistant()
                        .orElseThrow(() -> new IllegalStateException("last played assistant is not present"))
                        .getOrderValue();
                if (currAssistantValue == currMin) {
                    if (isBeforeCurrent(i)) {
                        currentIndex = i;
                    }
                }
                if (currAssistantValue < currMin) {
                    currMin = currAssistantValue;
                    currentIndex = i;
                }
            }
        }
    }


}
