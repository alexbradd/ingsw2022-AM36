package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for IslandList
 */
class IslandListTest {
    private Player player1, player2;
    private IslandList list;

    /**
     * Setup fresh players and a fresh list for each test
     */
    @BeforeEach
    void setUp() {
        list = new IslandList();
        player1 = new Player("Napoleon");
        player2 = new Player("Cesar");
    }

    /**
     * Tests that get() correctly gets elements withing bounds
     */
    @Test
    void getOOB() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(123));
    }

    /**
     * Tests creating groups out of two new leafs
     */
    @Test
    void testScrubLeafs() {
        assertEquals(list.getNumOfGroups(), 12);

        list.get(0).conquer(player1);
        list.get(1).conquer(player1);
        assertEquals(list.getNumOfGroups(), 11);

        list.get(2).conquer(player2);
        list.get(3).conquer(player2);
        assertEquals(list.getNumOfGroups(), 10);
    }

    /**
     * Tests merging a new leaf into an already existing group
     */
    @Test
    void testScrubLeafGroup() {
        assertEquals(list.getNumOfGroups(), 12);

        list.get(0).conquer(player1);
        list.get(1).conquer(player1);
        assertEquals(list.getNumOfGroups(), 11);

        list.get(2).conquer(player1);
        assertEquals(list.getNumOfGroups(), 10);
    }
}