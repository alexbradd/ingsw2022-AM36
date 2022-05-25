package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoundedStudentContainerTest {

    private BoundedStudentContainer bounded3;

    /**
     * Creates a new BoundedContainer instance and checks the edge cases of the constructor.
     */
    @DisplayName("Constructor and initialization")
    @BeforeEach
    void setUp() {
        bounded3 = new BoundedStudentContainer(3);

        assertThrows(IllegalArgumentException.class,
                () -> new BoundedStudentContainer(0));
        assertThrows(IllegalArgumentException.class,
                () -> new BoundedStudentContainer(null));
    }

    /**
     * Tests for the upper bound and exceptions throwing of the add() method.
     */
    @Test
    @DisplayName("Adding students to the BoundedContainer")
    void addTest() {
        Student s1 = new Student(PieceColor.RED);
        Student s2 = new Student(PieceColor.BLUE);
        Student s3 = new Student(PieceColor.PINK);
        Student s4 = new Student(PieceColor.YELLOW);

        assertAll(() -> bounded3 = bounded3.add(s1).add(s2).add(s3));

        assertThrows(ContainerIsFullException.class,
                () -> bounded3.add(s4));

        assertThrows(IllegalArgumentException.class,
                () -> bounded3.add(null));

    }
}