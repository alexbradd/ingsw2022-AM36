package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MotherNature
 */
class MotherNatureTest {
    private MotherNature mn;
    private List<Island> list;

    /**
     * Creates a new IslandList and MotherNature for each test
     */
    @BeforeEach
    void setUp() {
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
        mn = new MotherNature(list, 0);
    }

    /**
     * Bound check
     */
    @Test
    void boundCheck() {
        assertThrows(IllegalArgumentException.class, () -> new MotherNature(null, 0));
        assertThrows(IllegalArgumentException.class, () -> new MotherNature(list, -1));
        assertThrows(IllegalArgumentException.class, () -> new MotherNature(list, 20));
        assertThrows(IllegalArgumentException.class, () -> new MotherNature(null));
        assertThrows(IllegalArgumentException.class, () -> mn.move(null, 0));
        assertThrows(IllegalArgumentException.class, () -> mn.move(list, 0));
        assertThrows(IllegalArgumentException.class, () -> mn.move(null, -15));
        assertThrows(IllegalArgumentException.class, () -> mn.move(list, -15));
    }

    /**
     * Check if mother nature correctly iterates cyclically
     */
    @Test
    void movement() {
        for (int i = 0; i < list.size(); i++)
            mn = mn.move(list, 1);
        assertEquals(0, mn.getCurrentIslandId());
    }

    /**
     * Check if mother nature correctly resumes iteration on island changes
     */
    @Test
    void testResume() {
        mn = new MotherNature(list, 0);
        Island i = new Island(0)
                .updateTowers(t -> List.of(
                        new Tower(TowerColor.BLACK,
                                new Player("Ann",
                                        10,
                                        10,
                                        TowerColor.BLACK))))
                .merge(new Island(1)
                        .updateTowers(t -> List.of(new Tower(TowerColor.BLACK,
                                new Player("Ann",
                                        10,
                                        10,
                                        TowerColor.BLACK)))));
        mn = mn.move(List.of(i, new Island(2)), 1);
        assertEquals(2, mn.getCurrentIslandId());
    }
}