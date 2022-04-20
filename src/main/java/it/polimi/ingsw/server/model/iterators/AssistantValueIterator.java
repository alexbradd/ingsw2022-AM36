package it.polimi.ingsw.server.model.iterators;

import it.polimi.ingsw.server.model.Assistant;
import it.polimi.ingsw.server.model.Board;
import it.polimi.ingsw.server.model.Player;

import java.util.*;

/**
 * This class provides an iterator for a {@link List} of {@code Player}.
 * This implementation iterates on the list by ascending order of the mapped list's {@link Player} -
 * {@code player.lastPlayedAssistant.orderValue}.
 * For more information about the mapped values consult the {@link Player} and {@link Assistant} documentations.
 *
 * @author Mattia Busso
 */
public class AssistantValueIterator implements Iterator<Board> {

    /**
     * A copy of the list to iterate on (sorted using an {@link AssistantValueComparator}).
     */
    private final List<Board> list;

    /**
     * The index of the {@code Board} returned first.
     */
    private final int firstPlayedIndex;

    /**
     * Custom {@code startIndex} constructor.
     * Sets the custom {@code startIndex},
     * used to break ties in case of {@code Players} with the same {@code lastPlayedAssistant} value.
     *
     * @param list       the list to iterate on
     * @param startIndex the custom {@code startIndex} for the iteration
     * @throws IllegalArgumentException  if {@code list == null} or {@code list.size() == 0} (list is empty)
     * @throws IndexOutOfBoundsException if {@code startIndex} is out of range
     * @throws IllegalStateException     if a {@code Board} inside the {@code list} has no {@code lastPlayedAssistant}
     */
    public AssistantValueIterator(List<Board> list, int startIndex) throws IllegalArgumentException, IllegalStateException, IndexOutOfBoundsException {
        if(list == null) {
            throw new IllegalArgumentException("list shouldn't be null");
        }
        if(list.isEmpty()) {
            throw new IllegalArgumentException("list shouldn't be empty");
        }
        if(startIndex < 0 || startIndex >= list.size()) {
            throw new IndexOutOfBoundsException("invalid startIndex");
        }

        AssistantValueComparator comparator = new AssistantValueComparator(list, startIndex);
        this.list = new ArrayList<>(list);
        try {
            this.list.sort(comparator);
        }
        catch(IllegalStateException e) {
            throw new IllegalStateException("a Player inside the list doesn't have a lastPlayedAssistant");
        }

        firstPlayedIndex = list.indexOf(this.list.get(0));
    }

    /**
     * Creates a copy of the given constructor
     *
     * @param old the old constructor
     * @throws IllegalArgumentException if {@code old} is null
     */
    public AssistantValueIterator(AssistantValueIterator old) {
        if (old == null) throw new IllegalArgumentException("old shouldn't be null");
        this.list = new ArrayList<>(old.list);
        this.firstPlayedIndex = old.firstPlayedIndex;
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements or if the list is empty
     * @throws IllegalStateException  if a {@code Board} inside the {@code list} has no {@code lastPlayedAssistant}
     */
    public Board next() throws NoSuchElementException, IllegalStateException {
        if(list.isEmpty()) {
            throw new NoSuchElementException("no more elements to iterate on");
        }
        return list.remove(0);
    }

    /**
     * Returns {@code true} if the iteration has more elements
     * (in other words, returns {@code true} if {@code next()} would return an element, {@code false} otherwise).
     *
     * @return {@code true} if the iteration has more elements, {@code false} otherwise.
     */
    public boolean hasNext() {
        return !list.isEmpty();
    }

    /**
     * Returns the index of the first {@link Board} to be returned by the iterator.
     *
     * @return the index of the first player returned by {@code next()}
     * @throws IllegalStateException if no board has been returned yet
     */
    public int getFirstPlayedIndex() {
        return firstPlayedIndex;
    }

}
