// STUB from functionalize-islands
package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Island}
 */
public class IslandTest {
    List<Player> players;
    Island i;

    /**
     * Setup fresh environment before each test
     */
    @BeforeEach
    void setUp() {
        players = List.of(
                new Player("Napoleon"),
                new Player("Cesar"));
        i = new Island(0);
    }

    /**
     * Bound check.
     */
    @Test
    void withNull() {
        assertThrows(IllegalArgumentException.class, () -> new Island(null));
        assertThrows(IllegalArgumentException.class, () -> i.updateStudents(null));
        assertThrows(IllegalArgumentException.class, () -> i.updateTowers(null));
        assertThrows(IllegalArgumentException.class, () -> i.merge(null));
        assertThrows(IllegalArgumentException.class, () -> i.canBeMergedWith(null));
        assertThrows(IllegalArgumentException.class, () -> i.pushBlock(null));
    }

    /**
     * Check that a student update that returns null is ignored.
     */
    @Test
    void testNullContainer() {
        i = i.updateStudents(c -> null);
        i.updateStudents(c -> {
            assertNotNull(c);
            return c;
        });
    }

    /**
     * Check that instances of containers are not persisted through updates. Maybe forcing this behaviour prevents
     * memory optimization, but resource utilization is not our priority.
     */
    @Test
    void testContainerChange() {
        StudentContainer c = new StudentContainer();
        i = i.updateStudents(container -> c);
        i = i.updateStudents(container -> c);
        i.updateStudents(container -> {
            assertNotSame(container, c);
            assertTrue(container.getStudents().isEmpty());
            assertTrue(c.getStudents().isEmpty());
            return container;
        });
    }

    /**
     * Check that a student update has been applied correctly
     */
    @Test
    void blockingBoundCheck() {
        assertThrows(IllegalStateException.class, () -> i.popBlock());
    }

    /**
     * Check that a tower update that returns null is ignored.
     */
    @Test
    void testNullTowers() {
        i = i.updateTowers(t -> null);
        i.updateTowers(t -> {
            assertNotNull(t);
            return t;
        });
    }

    /**
     * Check that instances of tower lists are not persisted through updates. Maybe forcing this behaviour prevents
     * memory optimization, but resource utilization is not our priority.
     */
    @Test
    void testTowersChange() {
        List<Tower> l = new ArrayList<>();
        i = i.updateTowers(towers -> l);
        i = i.updateTowers(towers -> l);
        i.updateTowers(towers -> {
            assertNotSame(towers, l);
            assertTrue(towers.isEmpty());
            assertTrue(l.isEmpty());
            return towers;
        });
    }

    /**
     * Check whether you can put more towers than the island can hold
     */
    @Test
    void boundCheckUpdateTowers() {
        List<Tower> l1 = new ArrayList<>();
        for (int i = 0; i < 10; i++) l1.add(new Tower(TowerColor.BLACK, players.get(0)));

        assertThrows(IllegalArgumentException.class, () -> i.updateTowers(old -> l1));
    }

    /**
     * Check that a tower update has been applied correctly
     */
    @Test
    void testUpdateTowers() {
        List<Tower> l1 = List.of(new Tower(TowerColor.BLACK, players.get(0)));
        List<Tower> l2 = List.of(new Tower(TowerColor.WHITE, players.get(1)));

        i = i.updateTowers(old -> {
            assertTrue(old.isEmpty());
            return l1;
        });
        i = i.updateTowers(old -> {
            assertFalse(old.isEmpty());
            assertEquals(old, l1);
            return l2;
        });
        i = i.updateTowers(old -> {
            assertFalse(old.isEmpty());
            assertEquals(old, l2);
            return old;
        });
    }

    /**
     * Check that adding mismatched towers makes everything explode
     */
    @Test
    void testMismatchedTowers() {
        assertThrows(IllegalArgumentException.class, () -> i.updateTowers(l ->
                List.of(new Tower(TowerColor.BLACK, players.get(0)), new Tower(TowerColor.WHITE, players.get(1)))));
    }

    /**
     * Check that two islands result mergeable in the correct cases
     */
    @Test
    void testCanBeMerged() {
        Island other = new Island(1);
        assertFalse(i.canBeMergedWith(other));
        assertFalse(other.canBeMergedWith(i));

        other = other.updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));
        assertFalse(i.canBeMergedWith(other));
        assertFalse(other.canBeMergedWith(i));

        i = i.updateTowers(t -> List.of(new Tower(TowerColor.WHITE, players.get(1))));
        assertFalse(i.canBeMergedWith(other));
        assertFalse(other.canBeMergedWith(i));

        i = i.updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));
        assertTrue(i.canBeMergedWith(other));
        assertTrue(other.canBeMergedWith(i));
    }

    /**
     * Check that a merge between islands creates a new island with the correct id order
     */
    @Test
    void testMergeSingleIds() {
        Island other = new Island(1).updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));
        i = i.updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));

        Island merge = i.merge(other);
        assertEquals(merge.getIds(), List.of(0, 1));
    }

    /**
     * Check that a merge between islands with multiple ids creates a new island that the correct id order
     */
    @Test
    void testMergeMultipleIds() {
        Island one = new Island(1).updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));
        Island two = new Island(2).updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));
        Island three = new Island(3).updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));
        i = i.updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));

        Island zeroOne = i.merge(one);
        Island twoThree = two.merge(three);

        Island zeroOneTwoThree = zeroOne.merge(twoThree);
        assertEquals(zeroOneTwoThree.getIds(), List.of(0, 1, 2, 3));
    }

    /**
     * Check that a merge between islands creates a new island that with a merged container
     */
    @Test
    void testMergeContainers() {
        Professor p1 = new Professor(PieceColor.RED),
                p2 = new Professor(PieceColor.BLUE);
        i = i.updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))))
                .updateStudents(c -> {
                    for (int i = 0; i < 10; i++) c = c.add(new Student(p1.getColor()));
                    return c;
                });
        Island other = new Island(1)
                .updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))))
                .updateStudents(c -> {
                    for (int i = 0; i < 10; i++) c = c.add(new Student(p2.getColor()));
                    return c;
                });

        Island merge = i.merge(other);
        merge.updateStudents(mergeContainer -> {
            i.updateStudents(c1 -> {
                other.updateStudents(c2 -> {
                    mergeContainer.getStudents().forEach(s ->
                            assertTrue(c1.getStudents().contains(s) || c2.getStudents().contains(s)));
                    return c2;
                });
                return c1;
            });
            return mergeContainer;
        });
    }

    /**
     * Check that a merge between islands creates a new island that with a merged list of towers
     */
    @Test
    void testMergeTowers() {
        Island other = new Island(1).updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));
        i = i.updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));

        Island merge = i.merge(other);
        merge.updateTowers(towers -> {
            assertEquals(2, towers.size());
            towers.forEach(t -> assertEquals(t.getColor(), TowerColor.BLACK));
            return towers;
        });
    }

    /**
     * Check that a merge between islands creates a new island that with the sum of blocks
     */
    @Test
    void testMergeBlock() {
        i = i.updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))))
                .pushBlock(new BlockCard(new Herbalist()))
                .pushBlock(new BlockCard(new Herbalist()));
        Island other = new Island(1)
                .updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))))
                .pushBlock(new BlockCard(new Herbalist()));
        Island another = new Island(2)
                .updateTowers(t -> List.of(new Tower(TowerColor.BLACK, players.get(0))));

        Island merge = i.merge(other).merge(another);
        assert (merge.getNumOfBlocks() == 3);
    }

    /**
     * Check that {@code popBlock()} complains if called on an unblocked island
     */
    @Test
    void boundCheckUnblocking() {
        assertThrows(IllegalStateException.class, () -> i.popBlock());
    }

    /**
     * Check that {@code pushBlock()} adds one block from the Island
     */
    @Test
    void testPushBlock() {
        i = i.pushBlock(new BlockCard(new Herbalist()))
                .pushBlock(new BlockCard(new Herbalist()))
                .pushBlock(new BlockCard(new Herbalist()));
        assertEquals(3, i.getNumOfBlocks());
    }

    /**
     * Check that {@code popBlock()} removes one block from the Island
     */
    @Test
    void testPopBlock() {
        i = i.pushBlock(new BlockCard(new Herbalist()))
                .popBlock()
                .getFirst();
        assertEquals(0, i.getNumOfBlocks());
    }
}