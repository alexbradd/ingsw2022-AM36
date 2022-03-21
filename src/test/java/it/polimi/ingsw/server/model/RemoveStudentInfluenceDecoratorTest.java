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
 * Test class for RemoveStudentInfluenceDecorator
 */
class RemoveStudentInfluenceDecoratorTest {
    private static InfluenceCalculator calculator;
    private static Professor professor1, professor2;
    private static Player player1;
    private Island island;

    /**
     * Sets up static variables.
     */
    @BeforeAll
    static void staticSetUp() {
        professor1 = new Professor(PieceColor.RED);
        professor2 = new Professor(PieceColor.GREEN);
        player1 = new Player("Napoleon", 1, 10, TowerColor.WHITE);

        calculator = new RemoveStudentInfluenceDecorator(new StandardInfluenceCalculator(), professor1.getColor());

        professor1.assign(player1);
        professor2.assign(player1);
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
     * Checks if student influence is removed correctly
     */
    @Test
    void removesStudentInfluence() {
        for (int i = 0; i < 10; i++)
            island.receiveStudent(new Student(professor1));
        for (int i = 0; i < 15; i++)
            island.receiveStudent(new Student(professor2));

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island);
        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertEquals(map.get(player1), 15);
    }

    /**
     * Checks if players that have 0 influence after decoration are removed from the map.
     */
    @Test
    void playerWithNoInfluenceIsRemoved() {
        for (int i = 0; i < 10; i++)
            island.receiveStudent(new Student(professor1));

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island);
        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertNull(map.get(player1));
    }
}