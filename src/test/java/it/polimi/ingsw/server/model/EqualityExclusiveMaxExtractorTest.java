package it.polimi.ingsw.server.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for EqualityExclusiveMaxExtractor.
 */
public class EqualityExclusiveMaxExtractorTest extends MaxExtractorTest {
    private static EqualityExclusiveMaxExtractor extractor;
    private static Player[] playerSet;

    /**
     * Sets up static variables used by tests.
     */
    @BeforeAll
    static void setUp() {
        extractor = new EqualityExclusiveMaxExtractor();
        playerSet = genPlayerSet();
    }

    /**
     * Tests if IllegalArgumentException is thrown in case of null map.
     */
    @Test
    void withNull() {
        assertThrows(IllegalArgumentException.class, () -> extractor.apply(null));
    }

    /**
     * Tests if IllegalArgumentException is thrown in case of empty map.
     */
    @Test
    void withEmptyMap() {
        Map<Player, Integer> map = new HashMap<>();
        assertFalse(extractor.apply(map).isPresent());
    }

    /**
     * Tests calculation with a set containing one maximum.
     */
    @Test
    void withUniqueMax() {
        Map<Player, Integer> map = new HashMap<>();
        map.put(playerSet[0], 2);
        map.put(playerSet[1], 1);
        map.put(playerSet[2], 2);
        map.put(playerSet[3], 3);

        Optional<Player> max = extractor.apply(map);
        assertTrue(max.isPresent());
        assertEquals(max.get(), playerSet[3]);
    }

    /**
     * Tests calculation with a set containing two maximum candidates.
     */
    @Test
    void withDuplicatedMax() {
        Map<Player, Integer> map = new HashMap<>();
        map.put(playerSet[0], 2);
        map.put(playerSet[1], 1);
        map.put(playerSet[2], 2);

        Optional<Player> max = extractor.apply(map);
        assertFalse(max.isPresent());
    }
}
