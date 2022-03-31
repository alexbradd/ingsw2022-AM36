package it.polimi.ingsw.server.model;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * This class provides a concrete implementation of the {@link PlayerListIterator} abstract class.
 * This implementation iterates on the list by ascending order of the mapped list's {@link Player} -
 * last played {@link Assistant} {@code orderValue} attribute.
 * For more information about the mapped values consult the {@link Player} and {@link Assistant} documentations.
 *
 * @author Mattia Busso
 * @see PlayerListIterator
 */
class AssistantValueIterator extends PlayerListIterator {

    /**
     * The starting index used to break ties in case of same-value assistants
     */
    private final int startIndex;

    /**
     * The index of the current {@code Player} pointed by the iterator.
     */
    private int currentIndex;

    /**
     * The value of the {@code lastPlayedAssistant} of the current {@code Player} pointed by the iterator.
     */
    private int currMin;

    /**
     * The index of the first {@link Player} to be returned by the iterator.
     */
    private int firstPlayedIndex;

    /**
     * A set of players the iterator hasn't returned yet.
     */
    private final Set<Player> unReturned;

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
     * Calls the constructor of the parent class {@link PlayerListIterator} and sets the custom {@code startIndex},
     * used to break ties in case of {@code Players} with the same {@code lastPlayedAssistant} value.
     *
     * @param list       the list to iterate on
     * @param startIndex the custom {@code startIndex} for the iteration
     * @throws IllegalArgumentException  if {@code list == null} or {@code list.size() == 0} (list is empty)
     * @throws IndexOutOfBoundsException if {@code startIndex} is out of range
     * @throws IllegalStateException     if a {@code Player} inside the {@code PlayerList} has no {@code lastPlayedAssistant}
     */
    AssistantValueIterator(PlayerList list, int startIndex) throws IllegalArgumentException, IllegalStateException, IndexOutOfBoundsException {
        super(list);
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
        unReturned.remove(getList().get(currentIndex));

    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements or if the list is empty
     * @throws IllegalStateException  if a {@code Player} inside the {@code PlayerList} has no {@code lastPlayedAssistant}
     */
    public Player next() throws NoSuchElementException, IllegalStateException {
        Player nextPlayer = getList().get(currentIndex);

        if (start) {
            firstPlayedIndex = currentIndex;
            start = false;
        }

        if (currMin == MIN_UPPER_BOUND) {
            throw new NoSuchElementException("no more elements to iterate on");
        }

        updateCurrentValues();

        if (currMin != MIN_UPPER_BOUND) {
            unReturned.remove(getList().get(currentIndex));
        }

        return nextPlayer;

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
     * Returns the index of the first {@link Player} to be returned by the iterator.
     *
     * @return the index of the first player returned by {@code next()}
     * @throws IllegalStateException if no player has been returned yet
     */
    int getFirstPlayedIndex() {
        if (firstPlayedIndex == -1) {
            throw new IllegalStateException("no player has been returned yet");
        }
        return firstPlayedIndex;
    }

    /**
     * Returns {@code true} if {@code i} is the index of a {@code Player} inside the list that comes before
     * the {@code Player} index by {@code currentIndex}.
     *
     * @param i the index of the {@code Player} to check the order of
     * @return {@code true} if {@code i} indexes a player that comes before the player indexed by currentIndex, {@code false} otherwise
     */
    private boolean isBeforeCurrent(int i) {
        return ((i < currentIndex)) || (currentIndex < startIndex && i > startIndex) || (i == startIndex);
    }

    /**
     * Private method that updates {@code currentIndex} and {@code currMin} after one iteration.
     *
     * @throws IllegalStateException if a {@code Player} inside the {@code PlayerList} has no {@code lastPlayedAssistant}
     */
    private void updateCurrentValues() throws IllegalStateException {
        currMin = MIN_UPPER_BOUND;
        for (int i = 0; i < getList().size(); i++) {
            if (unReturned.contains(getList().get(i))) {
                int currAssistantValue = getList().get(i).getLastPlayedAssistant()
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
