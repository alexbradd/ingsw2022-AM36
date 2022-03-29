package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Island
 */
class IslandTest {
    private Island island;
    private Herbalist blocker;
    private Player player;

    /**
     * Creates a fresh island, blocker and player for each test
     */
    @BeforeEach
    void setUp() {
        island = new Island(0);
        blocker = new Herbalist();
        player = new Player("Napoleon", 1, 10, TowerColor.WHITE);
    }

    /**
     * Tests that all methods correctly complain when called with null
     */
    @Test
    void withNull() {
        assertThrows(IllegalArgumentException.class, () -> island.receiveStudent(null));
        assertThrows(IllegalArgumentException.class, () -> island.conquer(null));
        assertThrows(IllegalArgumentException.class, () -> island.merge(null));
        assertThrows(IllegalArgumentException.class, () -> island.canBeMergedWith(null));
        assertThrows(IllegalArgumentException.class, () -> island.isRelatedTo(null));
        assertThrows(IllegalArgumentException.class, () -> island.pushBlock(null));
    }

    /**
     * Tests adding students to a leaf island
     */
    @Test
    void leafReceiveStudent() {
        assertTrue(island.getStudents().isEmpty());

        Student s1 = new Student(new Professor(PieceColor.BLUE));
        Student s2 = new Student(new Professor(PieceColor.RED));

        island.receiveStudent(s1);
        island.receiveStudent(s2);

        assertEquals(island.getStudents().size(), 2);
        assertTrue(island.getStudents().contains(s1));
        assertTrue(island.getStudents().contains(s2));
    }

    /**
     * Tests conquering a leaf island
     */
    @Test
    void leafConquer() {
        int playerTowers = player.getNumOfTowers();
        assertTrue(island.getControllingPlayer().isEmpty());
        island.conquer(player);
        assertTrue(island.getControllingPlayer().isPresent());
        assertEquals(island.getControllingPlayer().get(), player);
        assertEquals(player.getNumOfTowers(), playerTowers - 1);

        Player player2 = new Player("Cesar", 1, 10, TowerColor.BLACK);
        playerTowers = player.getNumOfTowers();
        int player2Towers = player2.getNumOfTowers();
        island.conquer(player2);
        assertTrue(island.getControllingPlayer().isPresent());
        assertEquals(island.getControllingPlayer().get(), player2);
        assertEquals(player2.getNumOfTowers(), player2Towers - 1);
        assertEquals(player.getNumOfTowers(), playerTowers + 1);
    }

    /**
     * Check for unlocking an unlocked island
     */
    @Test
    void blockingBoundCheck() {
        assertThrows(IllegalStateException.class, () -> island.popBlock());
    }

    /**
     * Tests stack invariant for blocks
     */
    @Test
    void sameNumberOfBlocksAndUnblocks() {
        assertDoesNotThrow(() -> {
            island.pushBlock(blocker.popBlock());
            island.pushBlock(blocker.popBlock());
            island.popBlock();
            island.popBlock();
        });
        assertFalse(island.isBlocked());
    }

    /**
     * Tests blocking and unblocking a leaf island
     */
    @Test
    void leafBlockingUnblocking() {
        assertFalse(island.isBlocked());

        island.pushBlock(blocker.popBlock());
        int blockerBlocks = blocker.getNumOfBlocks();
        assertTrue(island.isBlocked());

        island.popBlock();
        assertFalse(island.isBlocked());
        assertEquals(blocker.getNumOfBlocks(), blockerBlocks + 1);
    }

    /**
     * Tests if merging complains when it is supposed to
     */
    @Test
    void mergeBoundCheck() {
        Island child = new Island(1);
        Player player2 = new Player("Cesar", 1, 10, TowerColor.BLACK);

        assertThrows(IllegalArgumentException.class, () -> island.merge(child));

        island.conquer(player);
        island.merge(island);
        assertTrue(island.getParent().isEmpty());
        assertThrows(IllegalArgumentException.class, () -> island.merge(child));

        child.conquer(player2);
        assertThrows(IllegalArgumentException.class, () -> island.merge(child));
    }

    /**
     * Tests merging two leafs
     */
    @Test
    void mergeLeafs() {
        Island child = new Island(1);
        island.conquer(player);
        child.conquer(player);

        assertTrue(child.getParent().isEmpty());
        island.merge(child);
        assertTrue(island.isRelatedTo(child));
        assertTrue(child.isRelatedTo(island));
    }

    /**
     * Tests merging a group into a leaf
     */
    @Test
    void mergeGroupIntoLeaf() {
        Island child1 = new Island(1);
        Island child2 = new Island(2);

        island.conquer(player);
        child1.conquer(player);
        child2.conquer(player);
        child1.merge(child2);

        assertTrue(child1.isRelatedTo(child2));
        assertTrue(island.getParent().isEmpty());
        island.merge(child2);
        assertEquals(child1.getParent(), child2.getParent());
        assertTrue(island.getParent().isEmpty());
        assertEquals(Optional.of(island), child1.getParent());
    }

    /**
     * Tests merging a leaf into a group
     */
    @Test
    void mergeLeafIntoGroup() {
        Island child1 = new Island(1);
        Island child2 = new Island(2);

        island.conquer(player);
        child1.conquer(player);
        child2.conquer(player);

        child1.merge(child2);

        assertTrue(child1.isRelatedTo(child2));
        assertTrue(island.getParent().isEmpty());
        child1.merge(island);
        assertTrue(child1.getParent().isEmpty());
        assertEquals(island.getParent(), child2.getParent());
        assertEquals(island.getParent(), Optional.of(child1));
    }

    /**
     * Tests merging two groups
     */
    @Test
    void mergeGroups() {
        Island child1 = new Island(1);
        Island child2 = new Island(2);
        Island child3 = new Island(3);

        island.conquer(player);
        child1.conquer(player);
        child2.conquer(player);
        child3.conquer(player);

        island.merge(child1);
        child2.merge(child3);

        assertTrue(island.isRelatedTo(child1));
        assertTrue(child2.isRelatedTo(child3));
        assertTrue(island.getParent().isEmpty());
        assertTrue(child2.getParent().isEmpty());

        island.merge(child2);

        assertTrue(island.getParent().isEmpty());

        assertEquals(child1.getParent(), child2.getParent());
        assertEquals(child2.getParent(), child3.getParent());
        assertEquals(child1.getParent(), Optional.of(island));
    }

    /**
     * Tests that merging two island doesn't pull more towers from players
     */
    @Test
    void mergingDoesntRequireTowers() {
        Island child = new Island(1);
        island.conquer(player);
        child.conquer(player);

        int playerTowers = player.getNumOfTowers();
        island.merge(child);
        assertEquals(player.getNumOfTowers(), playerTowers);
    }

    /**
     * Tests if students are transferred to parent upon merging
     */
    @Test
    void studentTransfer() {
        Island child = new Island(1);
        island.conquer(player);
        child.conquer(player);

        Student s1 = new Student(new Professor(PieceColor.BLUE));
        Student s2 = new Student(new Professor(PieceColor.RED));

        child.receiveStudent(s1);
        child.receiveStudent(s2);
        island.merge(child);
        assertTrue(island.getStudents().contains(s1));
        assertTrue(island.getStudents().contains(s2));
        assertTrue(child.getStudents().contains(s1));
        assertTrue(child.getStudents().contains(s2));
    }

    /**
     * Tests lock propagation
     */
    @Test
    void mergeBlockedIntoUnblocked() {
        Island child = new Island(1);
        island.conquer(player);
        child.conquer(player);

        BlockCard b = blocker.popBlock();
        int blocks = blocker.getNumOfBlocks();
        child.pushBlock(b);

        island.merge(child);
        assertTrue(island.isBlocked());
        assertTrue(child.isBlocked());
        assertEquals(blocker.getNumOfBlocks(), blocks);
    }

    /**
     * Tests lock propagation
     */
    @Test
    void mergeBlockedIntoBlocked() {
        Island child = new Island(1);
        island.conquer(player);
        child.conquer(player);

        BlockCard b1 = blocker.popBlock();
        BlockCard b2 = blocker.popBlock();
        int blocks = blocker.getNumOfBlocks();
        island.pushBlock(b1);
        child.pushBlock(b2);

        island.merge(child);
        assertTrue(island.isBlocked());
        assertTrue(child.isBlocked());
        assertEquals(blocker.getNumOfBlocks(), blocks);
    }

    /**
     * Tests sending students to an island group
     */
    @Test
    void groupReceiveStudent() {
        Island child = new Island(1);
        island.conquer(player);
        child.conquer(player);
        island.merge(child);

        Student s1 = new Student(new Professor(PieceColor.BLUE));
        Student s2 = new Student(new Professor(PieceColor.RED));

        island.receiveStudent(s1);
        child.receiveStudent(s2);

        assertTrue(island.getStudents().contains(s1));
        assertTrue(island.getStudents().contains(s2));
        assertTrue(child.getStudents().contains(s1));
        assertTrue(child.getStudents().contains(s2));
    }

    /**
     * Tests blocking an island group
     */
    @Test
    void groupBlock() {
        Island child = new Island(1);
        island.conquer(player);
        child.conquer(player);
        island.merge(child);

        BlockCard b = blocker.popBlock();
        island.pushBlock(b);
        assertTrue(island.isBlocked());
        assertTrue(child.isBlocked());

        int blockerBlocks = blocker.getNumOfBlocks();
        island.popBlock();
        assertFalse(island.isBlocked());
        assertFalse(child.isBlocked());
        assertThrows(IllegalStateException.class, child::popBlock);
        assertEquals(blocker.getNumOfBlocks(), blockerBlocks + 1);
    }

    /**
     * Tests conquering an island group
     */
    @Test
    void groupConquer() {
        Island child = new Island(1);
        island.conquer(player);
        child.conquer(player);
        island.merge(child);

        int playerTowers = player.getNumOfTowers();
        assertEquals(island.getControllingPlayer(), child.getControllingPlayer());

        island.conquer(player);
        assertTrue(island.getControllingPlayer().isPresent());
        assertEquals(island.getControllingPlayer().get(), player);
        assertEquals(island.getControllingPlayer(), child.getControllingPlayer());
        assertEquals(player.getNumOfTowers(), playerTowers);

        Player player2 = new Player("Cesar", 1, 10, TowerColor.BLACK);
        playerTowers = player.getNumOfTowers();
        int player2Towers = player2.getNumOfTowers();
        island.conquer(player2);
        assertTrue(island.getControllingPlayer().isPresent());
        assertEquals(island.getControllingPlayer().get(), player2);
        assertEquals(island.getControllingPlayer(), child.getControllingPlayer());
        assertEquals(player2.getNumOfTowers(), player2Towers - 2);
        assertEquals(player.getNumOfTowers(), playerTowers + 2);
    }

    /**
     * Tests if the number of towers is computed correctly
     */
    @Test
    void numOfTowers() {
        Island child = new Island(1);

        island.conquer(player);
        child.conquer(player);
        island.merge(child);

        assertEquals(island.getNumOfTowers(), 2);
        assertEquals(child.getNumOfTowers(), 2);
    }
}
