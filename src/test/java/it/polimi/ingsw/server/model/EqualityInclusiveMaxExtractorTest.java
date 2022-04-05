package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for EqualityInclusiveMaxExtractor.
 */
public class EqualityInclusiveMaxExtractorTest extends MaxExtractorTest {
    private static EqualityInclusiveMaxExtractor extractor;
    private static Player favourite;
    private static Player[] playerSet;

    /**
     * Sets up static variables used by tests.
     */
    @BeforeAll
    static void setUp() {
        playerSet = genPlayerSet();
        favourite = new Player("Neo", 1, 1, TowerColor.WHITE);
        extractor = new EqualityInclusiveMaxExtractor(favourite);
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
     * Tests calculation with a set containing only one maximum. Note: the privileged player is not in the map.
     */
    @Test
    void withUniqueMaximumNoPrivileged() {
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
     * Tests calculation with a set containing two maximum candidates. Note: the privileged player is not in the map.
     */
    @Test
    void withNonUniqueMaximumNoPrivileged() {
        Map<Player, Integer> map = new HashMap<>();
        map.put(playerSet[0], 2);
        map.put(playerSet[1], 1);
        map.put(playerSet[2], 2);
        map.put(playerSet[3], 2);

        Optional<Player> max = extractor.apply(map);
        assertFalse(max.isPresent());
    }

    /**
     * Tests calculation with a set containing one maximum. Note: the privileged player is in the map, but it is not the
     * maximum.
     */
    @Test
    void withUniqueMaximumPrivilegedNotMax() {
        Map<Player, Integer> map = new HashMap<>();
        map.put(playerSet[0], 2);
        map.put(playerSet[1], 1);
        map.put(playerSet[2], 2);
        map.put(playerSet[3], 3);
        map.put(favourite, 0);

        Optional<Player> max = extractor.apply(map);
        assertTrue(max.isPresent());
        assertEquals(max.get(), playerSet[3]);
    }

    /**
     * Tests calculation with a set containing two maximum candidates. Note: the privileged player is in the map, but
     * it is not the maximum.
     */
    @Test
    void withNonUniqueMaximumPrivilegedNotMax() {
        Map<Player, Integer> map = new HashMap<>();
        map.put(playerSet[0], 2);
        map.put(playerSet[1], 1);
        map.put(playerSet[2], 2);
        map.put(playerSet[3], 2);
        map.put(favourite, 0);

        Optional<Player> max = extractor.apply(map);
        assertFalse(max.isPresent());
    }

    /**
     * Tests calculation with a set containing one maximum. Note: the privileged player is in the map, and it is the
     * maximum.
     */
    @Test
    void withUniqueMaximumPrivilegedMax() {
        Map<Player, Integer> map = new HashMap<>();
        map.put(playerSet[0], 2);
        map.put(playerSet[1], 1);
        map.put(playerSet[2], 2);
        map.put(playerSet[3], 3);
        map.put(favourite, 4);

        Optional<Player> max = extractor.apply(map);
        assertTrue(max.isPresent());
        assertEquals(max.get(), favourite);
    }

    /**
     * Tests calculation with a set containing two maximum candidates. Note: the privileged player is in the map and
     * it is between the maximum candidates.
     */
    @Test
    void withNonUniqueMaximumPrivilegedMax() {
        Map<Player, Integer> map = new HashMap<>();
        map.put(playerSet[0], 2);
        map.put(playerSet[1], 1);
        map.put(playerSet[2], 2);
        map.put(playerSet[3], 4);
        map.put(favourite, 4);

        Optional<Player> max = extractor.apply(map);
        assertTrue(max.isPresent());
        assertEquals(max.get(), favourite);
    }
}
