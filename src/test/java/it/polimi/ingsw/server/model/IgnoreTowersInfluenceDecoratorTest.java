package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.enums.TowerColor;
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

    /**
     * Sets up static variables.
     */
    @BeforeAll
    static void staticSetUp() {
        calculator = new IgnoreTowersInfluenceDecorator(new StandardInfluenceCalculator());
        player1 = new Player("Napoleon", 1, 10, TowerColor.WHITE);
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
     * Check if the extra tower influence is removed
     */
    @Test
    void removesTowerInfluence() {
        Professor p = new Professor(PieceColor.RED);
        p.assign(player1);

        Island child = new Island(1)
                .updateTowers(t -> List.of(new Tower(TowerColor.BLACK, player1)));
        island = island
                .updateTowers(t -> List.of(new Tower(TowerColor.BLACK, player1)))
                .merge(child)
                .updateStudents(c -> c.add(new Student(p)));

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island);

        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertEquals(map.get(player1), 1);
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

        Optional<Map<Player, Integer>> inf = calculator.calculateInfluences(island);
        assertTrue(inf.isPresent());
        Map<Player, Integer> map = inf.get();
        assertNull(map.get(player1));
    }
}