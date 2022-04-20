package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

}
