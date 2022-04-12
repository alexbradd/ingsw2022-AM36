package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for StandardInfluenceCalculatorTest.
 */
class StandardInfluenceCalculatorTest {
    private static InfluenceCalculator calculator;
    private static Professor professor1;
    private static Player player1, player2;
    private Island island;

    /**
     * Sets up static variables.
     */
    @BeforeAll
    static void staticSetUp() {
        calculator = new StandardInfluenceCalculator();
        player1 = new Player("Napoleon", 1, 10, TowerColor.BLACK);
        player2 = new Player("Cesar", 1, 10, TowerColor.WHITE);
        professor1 = new Professor(PieceColor.RED, player1);
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
     * Checks if passing an empty island returns an empty influence map.
     */
    @Test
    void emptyIslandEmptyMap() {
        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island);
        assertTrue(inf.isPresent());
        assertTrue(inf.get().isEmpty());
    }

    /**
     * Checks if passing a blocked island returns no influence map.
     */
    @Test
    void blockedIslandNoMap() {
        Herbalist blocker = new Herbalist();
        island.pushBlock(blocker.popBlock().getSecond());

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island);
        assertTrue(inf.isEmpty());
    }

    /**
     * Checks if student influence is assigned correctly
     */
    @Test
    void correctStudentInfluence() {
        for (int i = 0; i < 10; i++)
            island.receiveStudent(new Student(professor1));
        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island);

        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertEquals(map.get(player1), 10);
        assertNull(map.get(player2));
    }

    /**
     * Checks if tower influence is assigned correctly
     */
    @Test
    void correctTowerInfluence() {
        Island child = new Island(1);

        island.conquer(player1);
        child.conquer(player1);
        island.merge(child);

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island);

        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertEquals(map.get(player1), 2);
        assertNull(map.get(player2));
    }
}