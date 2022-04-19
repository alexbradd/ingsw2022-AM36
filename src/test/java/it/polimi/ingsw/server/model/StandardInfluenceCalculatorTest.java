package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
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
    private static List<Professor> professorList;
    private Island island;

    /**
     * Sets up static variables.
     */
    @BeforeAll
    static void staticSetUp() {
        calculator = new StandardInfluenceCalculator();
        player1 = new Player("Napoleon");
        player2 = new Player("Cesar");
        professor1 = new Professor(PieceColor.RED, player1);

        professorList = new ArrayList<>();
        professorList.add(professor1);
        professorList.add(new Professor(PieceColor.YELLOW));
        professorList.add(new Professor(PieceColor.GREEN));
        professorList.add(new Professor(PieceColor.BLUE));
        professorList.add(new Professor(PieceColor.PINK));
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
     * Checks if passing a blocked island returns no influence map.
     */
    @Test
    void blockedIslandNoMap() {
        island = island.pushBlock(new BlockCard(CharacterType.HERBALIST));

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island, professorList);
        assertTrue(inf.isEmpty());
    }

    /**
     * Checks if student influence is assigned correctly
     */
    @Test
    void correctStudentInfluence() {
        island = island.updateStudents(c -> {
            for (int i = 0; i < 10; i++) {
                c = c.add(new Student(professor1.getColor()));
            }
            return c;
        });

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island, professorList);

        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertEquals(10, map.get(player1));
        assertNull(map.get(player2));
    }

    /**
     * Checks if tower influence is assigned correctly
     */
    @Test
    void correctTowerInfluence() {
        Island child = new Island(1)
                .updateTowers(t -> List.of(new Tower(TowerColor.BLACK, player1)));
        island = island
                .updateTowers((t) -> List.of(new Tower(TowerColor.BLACK, player1)))
                .merge(child);

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island, professorList);

        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertEquals(map.get(player1), 2);
        assertNull(map.get(player2));
    }
}