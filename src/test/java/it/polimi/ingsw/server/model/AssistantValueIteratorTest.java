package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for the {@link AssistantValueIterator} class.
 * This test takes into consideration a List<{@link Board}> with different sizes.
 * For each {@code size} of the {@code PlayerList} every particular case of iteration
 * given by the disposition of the players (with their corresponding {@code lastPlayedAssistant} value)
 * inside the {@code PlayerList} is thoroughly tested.
 *
 * @author Mattia Busso
 * @see AssistantValueIterator
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AssistantValueIteratorTest {

    /**
     * The List<{@link Board}> to iterate on.
     */
    private List<Board> list;

    /**
     * The deck of assistants that each {@link Player} inside the list is going to have.
     *
     * @see Assistant
     */
    private List<Assistant> deck;

    /**
     * Initializes the list and populates it with players (all of whom are set up with a new deck).
     *
     * @param numPlayers number of players to be added to the list
     */
    private void createAndPopulateList(int numPlayers) {
        list = new ArrayList<>();
        for (int i = 0; i < numPlayers; i++) {
            Player p = new Player("p" + i);
            Board b = new Board(p, 5, 5, TowerColor.WHITE);
            b = b.receiveDeck(createDeck());
            list.add(b);
        }
    }

    /**
     * Creates a deck of 4 assistants.
     * The assistants have {@code orderValue} in ascending order from 1 to 4.
     */

    private List<Assistant> createDeck() {
        List<Assistant> deck = new ArrayList<>();

        deck.add(new Assistant(AssistantType.CHEETAH, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.OSTRICH, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.CAT, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.EAGLE, Mage.FAIRY));
        return deck;
    }

    /**
     * Test for the case of incorrect parameters.
     */
    @Test
    @DisplayName("Incorrect parameters test")
    void incorrectParametersTest() {
        createAndPopulateList(1);
        assertThrows(IndexOutOfBoundsException.class, () -> new AssistantValueIterator(list, -1));
        assertThrows(IndexOutOfBoundsException.class, () -> new AssistantValueIterator(list, 2));
    }

    /**
     * Tests the iterator if a player has no {@code lastPlayedAssistant}.
     */
    @Test
    @DisplayName("Incorrect state test (no last-played assistants)")
    void incorrectStateTest() {
        createAndPopulateList(1);
        assertThrows(IllegalStateException.class, () -> new AssistantValueIterator(list, 0));
    }

    /**
     * Tests the iterator on an empty list.
     */
    @Test
    @DisplayName("Empty list test")
    void emptyListTest() {
        list = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> new AssistantValueIterator(list, 0));
    }

    /**
     * Tests the iterator on a one-player list.
     */
    @Test
    @DisplayName("One-player list test")
    void onePlayerTest() {
        createAndPopulateList(1);
        list.set(0, list.get(0).playAssistant(AssistantType.CHEETAH));

        Iterator<Board> iterator = new AssistantValueIterator(list, 0);
        assertTrue(iterator.hasNext());
        assertEquals(list.get(0), iterator.next());
        assertFalse(iterator.hasNext());
    }

    /**
     * Tests the iterator on a two-players list.
     */
    @Nested
    @DisplayName("Two-players list tests")
    class twoPlayersTest {

        /**
         * Tests for the case: no repeated assistant values
         * (iterators' {@code startIndex} is irrelevant).
         */
        @Test
        @DisplayName("Case: no repeated values")
        void firstCase() {
            // List by assistant values: [1, 2]

            createAndPopulateList(2);

            list.set(0, list.get(0).playAssistant(AssistantType.CHEETAH));
            list.set(1, list.get(1).playAssistant(AssistantType.OSTRICH));

            Iterator<Board> iterator = new AssistantValueIterator(list, 0);

            assertTrue(iterator.hasNext());
            assertEquals(list.get(0), iterator.next());
            assertEquals(list.get(1), iterator.next());
            assertFalse(iterator.hasNext());

            // List by assistant values: [2, 1]

            createAndPopulateList(2);

            list.set(0, list.get(0).playAssistant(AssistantType.OSTRICH));
            list.set(1, list.get(1).playAssistant(AssistantType.CHEETAH));

            iterator = new AssistantValueIterator(list, 0);

            assertTrue(iterator.hasNext());
            assertEquals(list.get(1), iterator.next());
            assertEquals(list.get(0), iterator.next());
            assertFalse(iterator.hasNext());
        }

        /**
         * Tests for the case: repeated assistant values.
         */
        @Test
        @DisplayName("Case: repeated values")
        void secondCase() {
            // List by assistant values: [1, 1]

            createAndPopulateList(2);

            list.set(0, list.get(0).playAssistant(AssistantType.CHEETAH));
            list.set(1, list.get(1).playAssistant(AssistantType.CHEETAH));

            // StartIndex = 0
            Iterator<Board> iterator = new AssistantValueIterator(list, 0);

            assertTrue(iterator.hasNext());
            assertEquals(list.get(0), iterator.next());
            assertEquals(list.get(1), iterator.next());
            assertFalse(iterator.hasNext());

            // StartIndex = 1
            iterator = new AssistantValueIterator(list, 1);

            assertTrue(iterator.hasNext());
            assertEquals(list.get(1), iterator.next());
            assertEquals(list.get(0), iterator.next());
            assertFalse(iterator.hasNext());
        }

    }

    /**
     * Tests the iterator on a three-player list.
     */
    @Nested
    @DisplayName("Three-players list tests")
    class threePlayersTests {

        /**
         * Tests for the case: no repeated assistant values
         * (the iterators' {@code startIndex} is irrelevant).
         */
        @Test
        @DisplayName("Case: no repeated values")
        void firstCase() {
            // List by assistant values: [2, 3, 1]

            createAndPopulateList(3);

            list.set(0, list.get(0).playAssistant(AssistantType.values()[1]));
            list.set(1, list.get(1).playAssistant(AssistantType.values()[2]));
            list.set(2, list.get(2).playAssistant(AssistantType.values()[0]));

            Iterator<Board> iterator = new AssistantValueIterator(list, 0);

            assertTrue(iterator.hasNext());
            assertEquals(list.get(2), iterator.next());
            assertEquals(list.get(0), iterator.next());
            assertEquals(list.get(1), iterator.next());
            assertFalse(iterator.hasNext());

        }

        /**
         * Tests for the case: two-repeated assistant values.
         */
        @Nested
        @DisplayName("Case: twice-repeated assistant values")
        class secondCase {

            @Test
            @DisplayName("Repeated values are after the startIndex")
            void afterStartIndex() {
                // List by assistant values: [1, 2, 2]

                createAndPopulateList(3);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[0]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[1]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[1]));

                Iterator<Board> iterator = new AssistantValueIterator(list, 0);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(0), iterator.next());
                assertEquals(list.get(1), iterator.next());
                assertEquals(list.get(2), iterator.next());
                assertFalse(iterator.hasNext());

                // List by assistant values: [3, 2, 2]

                createAndPopulateList(3);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[2]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[1]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[1]));

                iterator = new AssistantValueIterator(list, 0);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(1), iterator.next());
                assertEquals(list.get(2), iterator.next());
                assertEquals(list.get(0), iterator.next());
                assertFalse(iterator.hasNext());
            }

            @Test
            @DisplayName("Repeated values are before the startIndex")
            void beforeStartIndex() {
                // List by assistant values: [2, 2, 1]

                createAndPopulateList(3);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[1]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[1]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[0]));

                Iterator<Board> iterator = new AssistantValueIterator(list, 2);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(2), iterator.next());
                assertEquals(list.get(0), iterator.next());
                assertEquals(list.get(1), iterator.next());
                assertFalse(iterator.hasNext());

                // List by assistant values: [2, 2, 3]

                createAndPopulateList(3);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[1]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[1]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[2]));

                iterator = new AssistantValueIterator(list, 2);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(0), iterator.next());
                assertEquals(list.get(1), iterator.next());
                assertEquals(list.get(2), iterator.next());
                assertFalse(iterator.hasNext());
            }

            @Test
            @DisplayName("startIndex is between repeated values")
            void betweenStartIndex() {
                // List by assistant values: [2, 1, 2]

                createAndPopulateList(3);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[1]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[0]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[1]));

                Iterator<Board> iterator = new AssistantValueIterator(list, 1);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(1), iterator.next());
                assertEquals(list.get(2), iterator.next());
                assertEquals(list.get(0), iterator.next());
                assertFalse(iterator.hasNext());

                // List by assistant values: [2, 3, 2]

                createAndPopulateList(3);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[1]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[2]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[1]));

                iterator = new AssistantValueIterator(list, 1);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(2), iterator.next());
                assertEquals(list.get(0), iterator.next());
                assertEquals(list.get(1), iterator.next());
                assertFalse(iterator.hasNext());
            }

        }

        /**
         * Tests for the case: three-repeated assistant values.
         */
        @Test
        @DisplayName("Case: three-repeated values")
        void thirdCase() {
            // List by assistant values: [1, 1, 1]

            createAndPopulateList(3);

            list.set(0, list.get(0).playAssistant(AssistantType.values()[0]));
            list.set(1, list.get(1).playAssistant(AssistantType.values()[0]));
            list.set(2, list.get(2).playAssistant(AssistantType.values()[0]));

            // startIndex = 2
            Iterator<Board> iterator = new AssistantValueIterator(list, 2);

            assertTrue(iterator.hasNext());
            assertEquals(list.get(2), iterator.next());
            assertEquals(list.get(0), iterator.next());
            assertEquals(list.get(1), iterator.next());
            assertFalse(iterator.hasNext());
        }
    }

    /**
     * Tests the iterator for a four-players list.
     */
    @Nested
    @DisplayName("Four-players list test")
    class fourPlayersTests {

        /**
         * Tests for the case: no repeated assistant values
         * (the iterators' {@code startIndex} is irrelevant).
         */
        @Test
        @DisplayName("Case: no repeated assistant values")
        void firstCase() {
            // List by assistant values: [2, 3, 1, 4]

            createAndPopulateList(4);

            list.set(0, list.get(0).playAssistant(AssistantType.values()[1]));
            list.set(1, list.get(1).playAssistant(AssistantType.values()[2]));
            list.set(2, list.get(2).playAssistant(AssistantType.values()[0]));
            list.set(3, list.get(3).playAssistant(AssistantType.values()[3]));

            Iterator<Board> iterator = new AssistantValueIterator(list, 0);

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
        class secondCase {

            @Test
            @DisplayName("Repeated values are after the startIndex")
            void afterStartIndex() {
                // List by assistant values: [1, 2, 2, 2]

                createAndPopulateList(4);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[0]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[1]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[1]));
                list.set(3, list.get(3).playAssistant(AssistantType.values()[1]));

                Iterator<Board> iterator = new AssistantValueIterator(list, 0);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(0), iterator.next());
                assertEquals(list.get(1), iterator.next());
                assertEquals(list.get(2), iterator.next());
                assertEquals(list.get(3), iterator.next());
                assertFalse(iterator.hasNext());

                // List by assistant values: [3, 2, 2, 2]

                createAndPopulateList(4);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[2]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[1]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[1]));
                list.set(3, list.get(3).playAssistant(AssistantType.values()[1]));

                iterator = new AssistantValueIterator(list, 0);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(1), iterator.next());
                assertEquals(list.get(2), iterator.next());
                assertEquals(list.get(3), iterator.next());
                assertEquals(list.get(0), iterator.next());
                assertFalse(iterator.hasNext());
            }

            @Test
            @DisplayName("Repeated values are before the startIndex")
            void beforeStartIndex() {
                // List by assistant values: [2, 2, 2, 1]

                createAndPopulateList(4);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[1]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[1]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[1]));
                list.set(3, list.get(3).playAssistant(AssistantType.values()[0]));

                Iterator<Board> iterator = new AssistantValueIterator(list, 2);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(3), iterator.next());
                assertEquals(list.get(2), iterator.next());
                assertEquals(list.get(0), iterator.next());
                assertEquals(list.get(1), iterator.next());
                assertFalse(iterator.hasNext());

                createAndPopulateList(4);

                // List by assistant values: [2, 2, 2, 3]

                createAndPopulateList(4);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[1]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[1]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[1]));
                list.set(3, list.get(3).playAssistant(AssistantType.values()[2]));

                iterator = new AssistantValueIterator(list, 3);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(0), iterator.next());
                assertEquals(list.get(1), iterator.next());
                assertEquals(list.get(2), iterator.next());
                assertEquals(list.get(3), iterator.next());
                assertFalse(iterator.hasNext());
            }

            @Test
            @DisplayName("startIndex is between repeated values")
            void betweenStartIndex() {
                // List by assistant values: [2, 1, 2, 2]

                createAndPopulateList(4);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[1]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[0]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[1]));
                list.set(3, list.get(3).playAssistant(AssistantType.values()[1]));

                Iterator<Board> iterator = new AssistantValueIterator(list, 1);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(1), iterator.next());
                assertEquals(list.get(2), iterator.next());
                assertEquals(list.get(3), iterator.next());
                assertEquals(list.get(0), iterator.next());
                assertFalse(iterator.hasNext());

                // List by assistant values: [2, 3, 2, 2]

                createAndPopulateList(4);

                list.set(0, list.get(0).playAssistant(AssistantType.values()[1]));
                list.set(1, list.get(1).playAssistant(AssistantType.values()[2]));
                list.set(2, list.get(2).playAssistant(AssistantType.values()[1]));
                list.set(3, list.get(3).playAssistant(AssistantType.values()[1]));

                iterator = new AssistantValueIterator(list, 1);

                assertTrue(iterator.hasNext());
                assertEquals(list.get(2), iterator.next());
                assertEquals(list.get(3), iterator.next());
                assertEquals(list.get(0), iterator.next());
                assertEquals(list.get(1), iterator.next());
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

            createAndPopulateList(4);

            list.set(0, list.get(0).playAssistant(AssistantType.values()[0]));
            list.set(1, list.get(1).playAssistant(AssistantType.values()[0]));
            list.set(2, list.get(2).playAssistant(AssistantType.values()[0]));
            list.set(3, list.get(3).playAssistant(AssistantType.values()[0]));

            // startIndex = 2
            Iterator<Board> iterator = new AssistantValueIterator(list, 2);

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

            createAndPopulateList(4);

            list.set(0, list.get(0).playAssistant(AssistantType.values()[0]));
            list.set(1, list.get(1).playAssistant(AssistantType.values()[1]));
            list.set(2, list.get(2).playAssistant(AssistantType.values()[0]));
            list.set(3, list.get(3).playAssistant(AssistantType.values()[1]));

            // startIndex = 0
            Iterator<Board> iterator = new AssistantValueIterator(list, 0);

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

        createAndPopulateList(4);

        list.set(0, list.get(0).playAssistant(AssistantType.values()[0]));
        list.set(1, list.get(1).playAssistant(AssistantType.values()[1]));
        list.set(2, list.get(2).playAssistant(AssistantType.values()[0]));
        list.set(3, list.get(3).playAssistant(AssistantType.values()[1]));

        AssistantValueIterator iterator = new AssistantValueIterator(list, 0);

        iterator.next();
        assertEquals(0, iterator.getFirstPlayedIndex());

        iterator = new AssistantValueIterator(list, 1);
        iterator.next();
        assertEquals(2, iterator.getFirstPlayedIndex());

    }


}
