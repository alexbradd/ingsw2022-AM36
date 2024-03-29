package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.PieceColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
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
    private static List<Professor> professorList;
    private Island island;

    /**
     * Sets up static variables.
     */
    @BeforeAll
    static void staticSetUp() {
        player1 = new Player("Napoleon");
        professor1 = new Professor(PieceColor.RED, player1);
        professor2 = new Professor(PieceColor.GREEN, player1);

        calculator = new RemoveStudentInfluenceDecorator(new StandardInfluenceCalculator(), professor1.getColor());

        professorList = List.of(professor1,
                professor2,
                new Professor(PieceColor.BLUE),
                new Professor(PieceColor.YELLOW),
                new Professor(PieceColor.PINK));
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
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateInfluences(null, professorList));
    }

    /**
     * Checks if passing an empty island returns an empty influence map.
     */
    @Test
    void emptyIslandEmptyMap() {
        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island, professorList);
        assertTrue(inf.isPresent());
        assertTrue(inf.get().isEmpty());
    }

    /**
     * Checks if student influence is removed correctly
     */
    @Test
    void removesStudentInfluence() {
        for (int i = 0; i < 10; i++)
            island = island.updateStudents(c -> c.add(new Student(professor1.getColor())));
        for (int i = 0; i < 15; i++)
            island = island.updateStudents(c -> c.add(new Student(professor2.getColor())));

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island, professorList);
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
            island = island.updateStudents(c -> c.add(new Student(professor1.getColor())));

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island, professorList);
        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertNull(map.get(player1));
    }
}