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
    private final int maxSize = 4;

    /**
     * Initializes the cloud and the compare_set.
     */
    @BeforeEach
    void initTest() {
        cloud = new Cloud(maxSize);
        compare_set = new HashSet<Student>();
        for(int i = 0; i < maxSize; i++) compare_set.add(new Student(PieceColor.BLUE));
        cloud.refillCloud(compare_set);
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
        assertEquals(compare_set, cloud.drainCloud());
        assertEquals(new HashSet<Student>(), cloud.drainCloud());
    }

}
