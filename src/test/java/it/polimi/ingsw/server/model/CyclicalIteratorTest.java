package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for CyclicalIterator
 */
class CyclicalIteratorTest {
    private static List<Island> list;
    private CyclicalIterator<Island> iterator;

    /**
     * Sets up the list and island control.
     */
    @BeforeAll
    static void setUpAll() {
        list = List.of(
                new Island(0),
                new Island(1),
                new Island(2),
                new Island(3),
                new Island(4),
                new Island(5),
                new Island(6),
                new Island(7),
                new Island(8),
                new Island(9),
                new Island(10),
                new Island(11));
    }

    /**
     * Creates a new iterator for each test
     */
    @BeforeEach
    void setUp() {
        iterator = new CyclicalIterator<>(list);
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