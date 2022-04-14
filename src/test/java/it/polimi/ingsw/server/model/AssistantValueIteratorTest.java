package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.iterators.AssistantValueIterator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the {@link AssistantValueIterator} class.
 * This test takes into consideration a {@code list} with fixed size and
 * tests the assistant-value order of players returned from the iterator.
 *
 * @author Mattia Busso
 * @see AssistantValueIterator
 */
public class AssistantValueIteratorTest {

    /**
     * Assumes that {@code assistantValues.size() == 4} and that the values inside {@code assistantValues} range from 1 to 4 max.
     * Returns a list of players with the corresponding assistants (given by their assistant' values) played ({@code list.size() == 4}).
     *
     * @param assistantValues an array of assistant values to be played by the corresponding players
     * @return a list of players
     */
    private List<Player> createAndPopulateList(int[] assistantValues) {
        List<Player> list = new ArrayList<>();
        AssistantType[] valuesToType = {AssistantType.CHEETAH, AssistantType.OSTRICH, AssistantType.CAT, AssistantType.EAGLE};
        List<Assistant> deck = new ArrayList<>();
        Arrays.stream(valuesToType).toList().forEach((assistantType) -> deck.add(new Assistant(assistantType, Mage.MAGE)));
        for(int i = 0; i < 4; i++) {
            Player p = new Player("p"+i, 10, 10, TowerColor.WHITE);
            p = p.receiveDeck(deck);
            p = p.playAssistant(valuesToType[assistantValues[i] - 1]);
            list.add(p);
        }
        return list;
    }

    /**
     * Test for the constructor.
     */
    @Test
    @DisplayName("Test for the constructor")
    void constructorTest() {
        assertThrows(IllegalArgumentException.class, () -> new AssistantValueIterator(null, 0));
        assertThrows(IllegalArgumentException.class, () -> new AssistantValueIterator(new ArrayList<>(), 0));

        List<Player> listOne = createAndPopulateList(new int[]{1, 1, 1, 1});
        assertThrows(IndexOutOfBoundsException.class, () -> new AssistantValueIterator(listOne, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> new AssistantValueIterator(listOne, listOne.size()));

        List<Player> listTwo = new ArrayList<>();
        listTwo.add(new Player("p0", 10, 10, TowerColor.WHITE));
        listTwo.add(new Player("p1", 10, 10, TowerColor.BLACK));
        assertThrows(IllegalStateException.class, () -> new AssistantValueIterator(listTwo, 0));
    }

    @Nested
    @DisplayName("Tests for the correctness of the iterators' output")
    class MainTests {

        /**
         * Tests for the case: no repeated assistant values
         * (the iterators' {@code startIndex} is irrelevant).
         */
        @Test
        @DisplayName("Case: no repeated assistant values")
        void noRepeatedCase() {
            // List by assistant values: [2, 3, 1, 4]

            List<Player> list = createAndPopulateList(new int[]{2, 3, 1, 4});

            AssistantValueIterator iterator = new AssistantValueIterator(list, 0);

            assertTrue(iterator.hasNext());
            assertEquals(list.get(2), iterator.next());
            assertEquals(list.get(0), iterator.next());
            assertEquals(list.get(1), iterator.next());
            assertEquals(list.get(3), iterator.next());
            assertFalse(iterator.hasNext());
        }

        /**
         * Tests for the case: two-repeated and three-repeated assistant values.
         */
        @Nested
        @DisplayName("Cases: twice-repeated and thrice-repeated assistant values")
        class SubTests {

            @Test
            @DisplayName("Repeated values are after the startIndex")
            void afterStartIndex() {
                // List by assistant values: [1, 2, 2, 2]

                List<Player> listOne = createAndPopulateList(new int[]{1, 2, 2, 2});

                AssistantValueIterator iterator = new AssistantValueIterator(listOne, 0);

                assertTrue(iterator.hasNext());
                assertEquals(listOne.get(0), iterator.next());
                assertEquals(listOne.get(1), iterator.next());
                assertEquals(listOne.get(2), iterator.next());
                assertEquals(listOne.get(3), iterator.next());
                assertFalse(iterator.hasNext());

                // List by assistant values: [3, 2, 2, 2]

                List<Player> listTwo = createAndPopulateList(new int[]{3, 2, 2, 2});

                iterator = new AssistantValueIterator(listTwo, 0);

                assertTrue(iterator.hasNext());
                assertEquals(listTwo.get(1), iterator.next());
                assertEquals(listTwo.get(2), iterator.next());
                assertEquals(listTwo.get(3), iterator.next());
                assertEquals(listTwo.get(0), iterator.next());
                assertFalse(iterator.hasNext());
            }

            @Test
            @DisplayName("Repeated values are before the startIndex")
            void beforeStartIndex() {
                // List by assistant values: [2, 2, 2, 1]

                List<Player> listOne = createAndPopulateList(new int[]{2, 2, 2, 1});

                AssistantValueIterator iterator = new AssistantValueIterator(listOne, 3);

                assertTrue(iterator.hasNext());
                assertEquals(listOne.get(3), iterator.next());
                assertEquals(listOne.get(0), iterator.next());
                assertEquals(listOne.get(1), iterator.next());
                assertEquals(listOne.get(2), iterator.next());
                assertFalse(iterator.hasNext());

                // List by assistant values: [2, 2, 2, 3]

                List<Player> listTwo = createAndPopulateList(new int[]{2, 2, 2, 3});

                iterator = new AssistantValueIterator(listTwo, 3);

                assertTrue(iterator.hasNext());
                assertEquals(listTwo.get(0), iterator.next());
                assertEquals(listTwo.get(1), iterator.next());
                assertEquals(listTwo.get(2), iterator.next());
                assertEquals(listTwo.get(3), iterator.next());
                assertFalse(iterator.hasNext());
            }

            @Test
            @DisplayName("startIndex is between repeated values")
            void betweenStartIndex() {
                // List by assistant values: [2, 1, 2, 2]

                List<Player> listOne = createAndPopulateList(new int[]{2, 1, 2, 2});

                AssistantValueIterator iterator = new AssistantValueIterator(listOne, 1);

                assertTrue(iterator.hasNext());
                assertEquals(listOne.get(1), iterator.next());
                assertEquals(listOne.get(2), iterator.next());
                assertEquals(listOne.get(3), iterator.next());
                assertEquals(listOne.get(0), iterator.next());
                assertFalse(iterator.hasNext());

                // List by assistant values: [2, 3, 2, 2]

                List<Player> listTwo = createAndPopulateList(new int[]{2, 3, 2, 2});

                iterator = new AssistantValueIterator(listTwo, 1);

                assertTrue(iterator.hasNext());
                assertEquals(listTwo.get(2), iterator.next());
                assertEquals(listTwo.get(3), iterator.next());
                assertEquals(listTwo.get(0), iterator.next());
                assertEquals(listTwo.get(1), iterator.next());
                assertFalse(iterator.hasNext());
            }

        }

        /**
         * Tests for the case: four-repeated assistant values.
         */
        @Test
        @DisplayName("Case: four-repeated values")
        void thirdCase() {
            // List by assistant values: [1, 1, 1, 1]

            List<Player> list = createAndPopulateList(new int[]{1, 1, 1, 1});

            // startIndex = 2
            AssistantValueIterator iterator = new AssistantValueIterator(list, 2);

            assertTrue(iterator.hasNext());
            assertEquals(list.get(2), iterator.next());
            assertEquals(list.get(3), iterator.next());
            assertEquals(list.get(0), iterator.next());
            assertEquals(list.get(1), iterator.next());
            assertFalse(iterator.hasNext());
        }

        @Test
        @DisplayName("Case: multiple-repeated values")
        void fourthCase() {
            // List by assistant values: [1, 2, 1, 2]

            List<Player> list = createAndPopulateList(new int[]{1, 2, 1, 2});

            // startIndex = 0
            AssistantValueIterator iterator = new AssistantValueIterator(list, 0);

            assertTrue(iterator.hasNext());
            assertEquals(list.get(0), iterator.next());
            assertEquals(list.get(2), iterator.next());
            assertEquals(list.get(1), iterator.next());
            assertEquals(list.get(3), iterator.next());
            assertFalse(iterator.hasNext());
        }

    }

    /**
     * Test for the {@code getFirstPlayedIndex()} method.
     */
    @Test
    @DisplayName("getFirstPlayedIndex() method test")
    void getFirstPlayedIndexTest() {
        // List by assistant values: [1, 2, 1, 2]

        List<Player> list = createAndPopulateList(new int[]{1, 2, 1, 2});

        AssistantValueIterator iterator = new AssistantValueIterator(list, 0);

        iterator.next();
        assertEquals(0, iterator.getFirstPlayedIndex());

        iterator = new AssistantValueIterator(list, 1);
        iterator.next();
        assertEquals(2, iterator.getFirstPlayedIndex());

    }

}
