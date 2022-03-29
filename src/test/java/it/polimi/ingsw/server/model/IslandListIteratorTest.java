package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for IslandListIterator
 */
class IslandListIteratorTest {
    private static IslandList list;
    private IslandListIterator iterator;

    /**
     * Sets up the list and island control.
     */
    @BeforeAll
    static void setUpAll() {
        Player player1 = new Player("Napoleon", 1, 10, TowerColor.WHITE);
        Player player2 = new Player("Cesar", 1, 10, TowerColor.BLACK);
        list = new IslandList();
        list.get(0).conquer(player1);
        list.get(1).conquer(player1);
        list.get(2).conquer(player2);
        list.get(3).conquer(player2);
    }

    /**
     * Creates a new iterator for each test
     */
    @BeforeEach
    void setUp() {
        iterator = list.groupIterator();
    }

    /**
     * Tests if groups are skipped
     */
    @Test
    void groupSkipping() {
        Island i;

        i = iterator.next();
        assertEquals(i, list.get(0));

        i = iterator.next();
        assertEquals(i, list.get(2));

        i = iterator.next();
        assertEquals(i, list.get(4));
    }

    /**
     * Tests if the iteration is cyclical
     */
    @Test
    void cyclicalIteration() {
        Island i;
        do {
            i = iterator.next();
        } while (!i.equals(list.get(0)));
    }
}