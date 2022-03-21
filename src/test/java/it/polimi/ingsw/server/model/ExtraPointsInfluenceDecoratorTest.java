package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ExtraPointsInfluenceDecorator
 */
class ExtraPointsInfluenceDecoratorTest {
    private static InfluenceCalculator calculator;
    private static Player player1;
    private Island island;

    /**
     * Sets up static variables.
     */
    @BeforeAll
    static void staticSetUp() {
        player1 = new Player("Napoleon", 1, 10, TowerColor.WHITE);
        calculator = new ExtraPointsInfluenceDecorator(new StandardInfluenceCalculator(), player1, 2);
    }

    /**
     * Creates a new island before each test.
     */
    @BeforeEach
    void setUp() {
        island = new Island(0);
    }

    /**
     * Checks that methods complain when passed null
     */
    @Test
    void withNull() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateInfluences(null));
    }

    /**
     * Checks if the extra influence is added correctly
     */
    @Test
    void addsExtraInfluence() {
        island.conquer(player1);

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island);

        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertEquals(map.get(player1), 3);
    }

    /**
     * Checks if favourite player that should have had 0 influence, after decoration is added to map.
     */
    @Test
    void playerThatHadNotInfluenceIsAdded() {
        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island);

        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertEquals(map.get(player1), 2);
    }
}