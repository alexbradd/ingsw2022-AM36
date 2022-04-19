package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.ColorIsFullException;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;

class HallTest {

    private Hall hall, hallCopy;


    /**
     * Initialize the hall instances and checks the constructor pre-conditions.
     */
    @DisplayName("Constructor and initialization")
    @BeforeEach
    void setUp() {
        hall = new Hall();
        hallCopy = new Hall(hall);

        assertThrows(IllegalArgumentException.class,
                () -> new Hall(null));
    }

    /**
     * Checks that, if the Hall is filled with the maximum number of students, then adding a new player is not permitted.
     */
    @Test
    @DisplayName("Adding students test")
    void addTest() {
        assertAll(() -> {
            for (PieceColor color : PieceColor.values()) {
                for (int i = 0; i < Hall.maxColorSize; i++) {
                    hall = hall.add(new Student(color));
                }
            }
        });

        assertEquals(Hall.maxColorSize * PieceColor.values().length, hall.size());

        assertThrows(ContainerIsFullException.class,
                () -> hall.add(new Student(PieceColor.RED)));

        assertThrows(IllegalArgumentException.class,
                () -> hall.add(null));
    }

    /**
     * Checks that, if the hall is filled with the maximum number of students of a certain color, then adding a student
     * of that color is not allowed, this time indicating (with a {@link ColorIsFullException}) that the container is not
     * completely full.
     */
    @Test
    @DisplayName("Adding students test 2")
    void addTest2() {

        assertAll(() -> {
            for (int i = 0; i < Hall.maxColorSize; i++) {
                hall = hall.add(new Student(PieceColor.RED));
            }
        });

        assertThrows(ColorIsFullException.class, () -> hall.add(new Student(PieceColor.RED)));

    }

    /**
     * Tests the removal behaviour, in the edge case in which there are no students inside the hall.
     */
    @Test
    @DisplayName("Removing students test")
    void removeTest() {
        assertThrows(EmptyContainerException.class,
                () -> hall.remove());

        assertThrows(EmptyContainerException.class,
                () -> hall.remove(PieceColor.RED));

        assertThrows(IllegalArgumentException.class,
                () -> hall.remove(null));

        Student student = new Student(PieceColor.RED);
        hall = hall.add(student);

        assertThrows(EmptyStackException.class, () -> hall.remove(PieceColor.BLUE));
        assertAll(() -> hall.remove(PieceColor.RED));
    }
}