package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.iterators.ClockWiseIterator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the {@link ClockWiseIterator} class.
 * This test takes into consideration a {@code list} with fixed size and
 * tests the clock-wise property of the iterator.
 *
 * @author Mattia Busso
 * @see ClockWiseIterator
 */
public class ClockWiseIteratorTest {

    /**
     * The list to iterate on.
     */
    private List<Player> list;

    /**
     * Initial setup.
     * The fixed size of the list is four.
     * We populate the {@code list} with {@link Player} objects since the in-game use case of the iterator is
     * to scan a list of players, but since the iterator works on abstract-type lists, it effectively is irrelevant.
     */
    @BeforeEach
    void createAndPopulateList() {
        list = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            list.add(new Player("p" + i, 10, TowerColor.WHITE));
        }
    }

    /**
     * Tests for the constructors.
     * Checks the behaviour of the constructors in case of incorrect parameters.
     */
    @Test
    @DisplayName("Custom startIndex constructor test")
    void customConstructorTest() {
        assertThrows(IllegalArgumentException.class, () -> new ClockWiseIterator<Player>(null, 0));
        assertThrows(IllegalArgumentException.class, () -> new ClockWiseIterator<>(new ArrayList<>(), 0));
        assertThrows(IndexOutOfBoundsException.class, () -> new ClockWiseIterator<>(list, list.size()));
        assertThrows(IndexOutOfBoundsException.class, () -> new ClockWiseIterator<>(list, -1));
    }

    /**
     * Test for the clock-wise property of the iterator.
     * Performs one cycle.
     * Takes the index from which to start iterating as a parameter.
     *
     * @param startIndex the start index from which to start iterating
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @DisplayName("Test for the clock-wise property - parameter: startIndex")
    void clockwisePropertyTest(int startIndex) {
        int currentIndex = startIndex;
        ClockWiseIterator<Player> iterator = new ClockWiseIterator<>(list, startIndex);
        do {
            assertTrue(iterator.hasNext());
            assertEquals(list.get(currentIndex), iterator.next());
            currentIndex++;
            if(currentIndex == list.size()) currentIndex = 0;
        }
        while(startIndex != currentIndex);
        assertFalse(iterator.hasNext());
    }

}