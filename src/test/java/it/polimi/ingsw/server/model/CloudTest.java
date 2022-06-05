package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for Cloud class.
 *
 * @author Mattia Busso
 */
public class CloudTest {
    private Cloud cloud;
    private Set<Student> compare_set;

    /**
     * Initializes the cloud and the compare_set.
     */
    @BeforeEach
    void initTest() {
        int maxSize = 4;
        cloud = new Cloud(maxSize);
        compare_set = new HashSet<>();
        for (int i = 0; i < maxSize; i++) compare_set.add(new Student(PieceColor.BLUE));
        cloud = cloud.refillCloud(compare_set);
    }

    /**
     * Bound check methods
     */
    @Test
    void boundCheck() {
        assertThrows(IllegalArgumentException.class, () -> new Cloud(-1));
        assertThrows(IllegalArgumentException.class, () -> new Cloud(0));
        assertThrows(IllegalArgumentException.class, () -> cloud.refillCloud(null));
    }

    /**
     * Test for refillCloud() method.
     */
    @Test
    void refillCloudTest() {
        compare_set.add(new Student(PieceColor.BLUE));
        assertThrows(IllegalArgumentException.class, () -> cloud.refillCloud(compare_set));
    }

    /**
     * Test for drainCloud() method.
     */
    @Test
    void drainCloudTest() {
        Set<Student> compare_set = cloud.getStudents();
        Tuple<Cloud, Set<Student>> firstDrain = cloud.drainCloud();
        assertEquals(compare_set, firstDrain.getSecond());
        Tuple<Cloud, Set<Student>> secondDrain = firstDrain.getFirst().drainCloud();
        assertEquals(new HashSet<Student>(), secondDrain.getSecond());
    }

    /**
     * Checks that size() respects its contract
     */
    @Test
    void size() {
        Cloud c = new Cloud(3).add(new Student(PieceColor.RED));
        assertEquals(1, c.size());
    }

    /**
     * Checks that size(PieceColor) respects its contract
     */
    @Test
    void sizeColor() {
        Cloud c = new Cloud(3).add(new Student(PieceColor.RED));
        assertEquals(1, c.size(PieceColor.RED));
        assertEquals(0, c.size(PieceColor.BLUE));
        assertEquals(0, c.size(PieceColor.GREEN));
        assertEquals(0, c.size(PieceColor.YELLOW));
        assertEquals(0, c.size(PieceColor.PINK));
    }

    /**
     * Checks that add() respects its contract
     */
    @Test
    void add() {
        Cloud pre = new Cloud(3);

        assertThrows(IllegalArgumentException.class, () -> pre.add(null));

        Cloud post = pre.add(new Student(PieceColor.RED));
        assertEquals(0, pre.size());
        assertEquals(1, post.size());
        assertEquals(1, post.size(PieceColor.RED));
        assertNotSame(pre, post);

        assertAll(() -> {
            Cloud update = post;
            for (int i = 0; i < 2; i++)
                update = update.add(new Student(PieceColor.RED));
            Cloud finalUpdate = update;
            assertThrows(ContainerIsFullException.class, () -> finalUpdate.add(new Student(PieceColor.BLUE)));
        });
    }

    /**
     * Checks that remove() respects its contract
     */
    @Test
    void remove() {
        Cloud pre = new Cloud(3).add(new Student(PieceColor.RED));
        Tuple<Cloud, Student> post = pre.remove();
        assertEquals(1, pre.size());
        assertEquals(0, post.getFirst().size());
        assertEquals(PieceColor.RED, post.getSecond().getColor());
        assertNotSame(post.getFirst(), pre);

        assertThrows(EmptyContainerException.class, () -> post.getFirst().remove());
    }

    /**
     * Checks that remove(PieceColor) respects its contract
     */
    @Test
    void removeColor() {
        Cloud pre = new Cloud(3)
                .add(new Student(PieceColor.RED))
                .add(new Student(PieceColor.BLUE));

        assertThrows(EmptyStackException.class, () -> pre.remove(PieceColor.PINK));

        Tuple<Cloud, Student> post = pre.remove(PieceColor.RED);
        assertEquals(2, pre.size());
        assertEquals(0, post.getFirst().size(PieceColor.RED));
        assertEquals(1, post.getFirst().size(PieceColor.BLUE));
        assertEquals(PieceColor.RED, post.getSecond().getColor());
        assertNotSame(post.getFirst(), pre);

        Tuple<Cloud, Student> post2 = post.getFirst().remove(PieceColor.BLUE);
        assertThrows(EmptyContainerException.class, () -> post2.getFirst().remove(PieceColor.BLUE));
    }
}
