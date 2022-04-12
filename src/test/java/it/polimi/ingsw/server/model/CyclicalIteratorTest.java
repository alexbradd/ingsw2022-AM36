package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.*;
import it.polimi.ingsw.server.model.iterators.CyclicalIterator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the {@link CyclicalIterator} class.
 * This test takes into consideration a {@code list} of fixed size and tests
 * the cyclical and clock-wise properties of the iterator.
 *
 * @author Mattia Busso
 * @see CyclicalIterator
 */
public class CyclicalIteratorTest {

    /**
     * The list to iterate on.
     */
    private List<Island> list;

    /**
     * Initial setup.
     * The fixed size of the list is four.
     * We populate the {@code list} with {@link Island} objects since the in-game use case of the iterator is
     * to scan a list of islands, but since the iterator works on abstract-type lists, it effectively is irrelevant.
     */
    @BeforeEach
    void createAndPopulateList() {
        list = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            list.add(new Island(i));
        }
    }

    /**
     * Tests for the constructors.
     * Checks the behaviour of the constructors in case of incorrect parameters.
     */
    @Nested
    @DisplayName("Constructors tests")
    class ConstructorsTest {

        @Test
        @DisplayName("Basic constructor test")
        void basicConstructorTest() {
            assertThrows(IllegalArgumentException.class, () -> new CyclicalIterator<Island>(null));
            assertThrows(IllegalArgumentException.class, () -> new CyclicalIterator<>(new ArrayList<>()));
        }

        @Test
        @DisplayName("Custom startIndex constructor test")
        void customConstructorTest() {
            assertThrows(IllegalArgumentException.class, () -> new CyclicalIterator<Island>(null, 0));
            assertThrows(IllegalArgumentException.class, () -> new CyclicalIterator<>(new ArrayList<>(), 0));
            assertThrows(IndexOutOfBoundsException.class, () -> new CyclicalIterator<>(list, list.size()));
            assertThrows(IndexOutOfBoundsException.class, () -> new CyclicalIterator<>(list, -1));
        }

    }

    /**
     * Test for the cyclical property of the iterator.
     * Uses the basic constructor ({@code startIndex = 0}).
     * Takes the number of cycles to perform as a parameter.
     *
     * @param numCycles the number of cycles to perform
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 10, 100, 1000})
    @DisplayName("Test for the cyclical property - parameter: numCycles")
    void cyclicalPropertyTest(int numCycles) {
        int numCyclesLeft = numCycles;
        CyclicalIterator<Island> iterator = new CyclicalIterator<>(list);
        while(numCyclesLeft > 0) {
            for (Island island : list) {
                assertTrue(iterator.hasNext());
                assertEquals(island, iterator.next());
            }
            numCyclesLeft--;
        }
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
        CyclicalIterator<Island> iterator = new CyclicalIterator<>(list, startIndex);
        do {
            assertTrue(iterator.hasNext());
            assertEquals(list.get(currentIndex), iterator.next());
            currentIndex++;
            if(currentIndex == list.size()) currentIndex = 0;
        }
        while(startIndex != currentIndex);
    }

}
