package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.TowerColor;
import it.polimi.ingsw.server.model.iterators.ClockWiseIterator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the {@link ClockWiseIterator} class.
 * This test takes into consideration a List<{@link Player}>.
 * For each {@code size} of the {@code List<Player>} every particular case of iteration is tested.
 *
 * @author Mattia Busso
 * @see ClockWiseIterator
 */
public class ClockWiseIteratorTest {

    /**
     * The List<{@link Player}> to iterate on.
     */
    private List<Board> list;

    /**
     * Initializes the list and populates it with players.
     *
     * @param numPlayers number of players to be added to the list
     */
    private void createAndPopulateList(int numPlayers) {
        list = new ArrayList<>();
        for(int i = 0; i < numPlayers; i++) {
            list.add(new Board(new Player("p"), 7, 7, TowerColor.WHITE));
        }
    }

    /**
     * Test for the case of incorrect parameters.
     */
    @Test
    @DisplayName("Incorrect parameters test")
    void incorrectParameters() {
        createAndPopulateList(1);
        assertThrows(IndexOutOfBoundsException.class, () -> new ClockWiseIterator(list, -1));
    }

    /**
     * Tests the iterator on an empty list.
     */
    @Test
    @DisplayName("Empty list test")
    void emptyListTest() {
        list = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> new ClockWiseIterator(list, 0));
    }

    /**
     * Tests the iterator on a one-player list.
     */
    @Test
    @DisplayName("One-player list test")
    void onePlayerTest() {
        createAndPopulateList(1);

        ClockWiseIterator iterator = new ClockWiseIterator(list, 0);

        assertTrue(iterator.hasNext());
        assertEquals(list.get(0), iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Tests the iterator on a two-players list.
     */
    @Test
    @DisplayName("Two-players list test")
    void twoPlayersTest() {
        createAndPopulateList(2);

        // startIndex = 0
        ClockWiseIterator iterator = new ClockWiseIterator(list, 0);

        assertTrue(iterator.hasNext());
        assertEquals(list.get(0), iterator.next());
        assertEquals(list.get(1), iterator.next());
        assertFalse(iterator.hasNext());

        // startIndex = 1
        iterator = new ClockWiseIterator(list, 1);

        assertTrue(iterator.hasNext());
        assertEquals(list.get(1), iterator.next());
        assertEquals(list.get(0), iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Tests the iterator on a three-players list.
     */
    @Test
    @DisplayName("Three-players list test")
    void threePlayersTest() {
        createAndPopulateList(3);

        // startIndex = 0
        ClockWiseIterator iterator = new ClockWiseIterator(list, 0);

        assertTrue(iterator.hasNext());
        assertEquals(list.get(0), iterator.next());
        assertEquals(list.get(1), iterator.next());
        assertEquals(list.get(2), iterator.next());
        assertFalse(iterator.hasNext());

        // startIndex = 1
        iterator = new ClockWiseIterator(list, 1);

        assertTrue(iterator.hasNext());
        assertEquals(list.get(1), iterator.next());
        assertEquals(list.get(2), iterator.next());
        assertEquals(list.get(0), iterator.next());
        assertFalse(iterator.hasNext());

        // startIndex = 2
        iterator = new ClockWiseIterator(list, 2);

        assertTrue(iterator.hasNext());
        assertEquals(list.get(2), iterator.next());
        assertEquals(list.get(0), iterator.next());
        assertEquals(list.get(1), iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    @DisplayName("Four-players list test")
    void fourPlayersTest() {
        createAndPopulateList(4);

        // startIndex = 0
        ClockWiseIterator iterator = new ClockWiseIterator(list, 0);

        assertTrue(iterator.hasNext());
        assertEquals(list.get(0), iterator.next());
        assertEquals(list.get(1), iterator.next());
        assertEquals(list.get(2), iterator.next());
        assertEquals(list.get(3), iterator.next());
        assertFalse(iterator.hasNext());

        // startIndex = 1
        iterator = new ClockWiseIterator(list, 1);

        assertTrue(iterator.hasNext());
        assertEquals(list.get(1), iterator.next());
        assertEquals(list.get(2), iterator.next());
        assertEquals(list.get(3), iterator.next());
        assertEquals(list.get(0), iterator.next());
        assertFalse(iterator.hasNext());

        // startIndex = 2
        iterator = new ClockWiseIterator(list, 2);

        assertTrue(iterator.hasNext());
        assertEquals(list.get(2), iterator.next());
        assertEquals(list.get(3), iterator.next());
        assertEquals(list.get(0), iterator.next());
        assertEquals(list.get(1), iterator.next());
        assertFalse(iterator.hasNext());

        // startIndex = 3
        iterator = new ClockWiseIterator(list, 3);

        assertTrue(iterator.hasNext());
        assertEquals(list.get(3), iterator.next());
        assertEquals(list.get(0), iterator.next());
        assertEquals(list.get(1), iterator.next());
        assertEquals(list.get(2), iterator.next());
        assertFalse(iterator.hasNext());
    }

}
