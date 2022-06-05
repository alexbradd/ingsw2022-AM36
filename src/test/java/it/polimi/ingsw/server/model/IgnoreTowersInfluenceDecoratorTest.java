package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.enums.TowerColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for IgnoreTowersInfluenceDecorators
 */
class IgnoreTowersInfluenceDecoratorTest {
    private static InfluenceCalculator calculator;
    private static Player player1;
    private Island island;
    List<Professor> professorList;

    /**
     * Sets up static variables.
     */
    @BeforeAll
    static void staticSetUp() {
        calculator = new IgnoreTowersInfluenceDecorator(new StandardInfluenceCalculator());
        player1 = new Player("Napoleon");
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
     * Check if the extra tower influence is removed
     */
    @Test
    void removesTowerInfluence() {
        Professor p =new Professor(PieceColor.RED, player1);
        professorList = List.of(p);

        Island child = new Island(1)
                .updateTowers(t -> List.of(new Tower(TowerColor.BLACK, player1)));
        island = island
                .updateTowers(t -> List.of(new Tower(TowerColor.BLACK, player1)))
                .merge(child)
                .updateStudents(c -> c.add(new Student(p.getColor())));

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island, professorList);

        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertEquals(1, map.get(player1));
    }

    /**
     * Checks if players that have 0 influence after decoration are removed from the map.
     */
    @Test
    void playerWithNoInfluenceIsRemoved() {
        Island child = new Island(1)
                .updateTowers(t -> List.of(new Tower(TowerColor.BLACK, player1)));
        island = island
                .updateTowers(t -> List.of(new Tower(TowerColor.BLACK, player1)))
                .merge(child);

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island, professorList);
        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertNull(map.get(player1));
    }
}