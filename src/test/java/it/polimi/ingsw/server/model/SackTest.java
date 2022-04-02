package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Test for Sack class.
 *
 * @author Mattia Busso
 */
public class SackTest {

    private Sack sack;
    private Set<Student> students_set;

    /**
     * Initialization of the sack and of the students_set used to check correctness of sendStudent() method.
     */
    @BeforeEach
    void initTest() {
        // initializing the sack and students_set and creating an array of professors for easier Students creation
        sack = new Sack();
        students_set = new HashSet<Student>();
        Professor[] professors = {
                new Professor(PieceColor.GREEN), new Professor(PieceColor.RED),
                new Professor(PieceColor.YELLOW), new Professor(PieceColor.PINK), new Professor(PieceColor.BLUE)
        };

        // Adding students to both the sack and the students_set
        // The students_set will be used to confront the player extracted from the sack with the ones added
        for(int i = 0; i < 5; i++) {
            int num_students_of_color;
            switch(i) {
                case 0: { num_students_of_color = 10; break; }
                case 1: { num_students_of_color = 20; break; }
                case 2: { num_students_of_color = 8; break; }
                default: {num_students_of_color = 5; break; }
            }
            for(int j = 0; j < num_students_of_color; j++) {
                Student curr_student = new Student(professors[i]);
                students_set.add(curr_student);
                sack.receiveStudent(curr_student);
            }
        }
    }

    /**
     * Test for sendStudent() method.
     */
    @Test
    void sendStudentTest() {
        while(!students_set.isEmpty()) {
            Student student_from_sack = sack.sendStudent();
            assertTrue(students_set.contains(student_from_sack));
            students_set.remove(student_from_sack);
        }
        assertThrows(IllegalStateException.class, () -> sack.sendStudent());
    }

}
