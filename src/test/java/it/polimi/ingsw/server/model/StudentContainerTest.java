package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.EmptyStackException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StudentContainerTest {

    private StudentContainer island;

    private Student s, s1;

    /**
     * Creates a new Container instance and adds a few students to it. It then checks the size() method behaviour.
     */
    @BeforeAll
    @DisplayName("Container constructor test and initialization")
    @Test
    void constructorTest() {
        island = new StudentContainer();
        s = new Student(PieceColor.BLUE);
        s1 = new Student(PieceColor.RED);
        island = island.add(s).add(s1);

        assertEquals(2, island.size());
        assertEquals(1, island.size(PieceColor.RED));
        assertEquals(1, island.size(PieceColor.BLUE));
        assertEquals(0, island.size(PieceColor.PINK));

    }

    /**
     * Tests the addition of a new student to the Container, and that add() throws an exception in case of null pointer.
     */
    @Test
    @DisplayName("Test for the addition of students")
    void addTest() {

        Student s2 = new Student(PieceColor.GREEN);
        island = island.add(s2);

        assertEquals(3, island.size());

        assertThrows(IllegalArgumentException.class,
                () -> island.add(null));

    }

    /**
     * Removes all the added students one by one, checking if the behaviour is correct via size() method and exceptions.
     */
    @Test
    @DisplayName("Test for the removal of students")
    void removeTest() {
        island = island.remove(PieceColor.GREEN).getFirst();

        island = island.remove(PieceColor.RED).getFirst();
        assertEquals(0, island.size(PieceColor.RED));
        assertEquals(1, island.size(PieceColor.BLUE));
        assertEquals(0, island.size(PieceColor.PINK));

        assertThrows(EmptyStackException.class,
                () -> island.remove(PieceColor.PINK));

        island = island.remove(PieceColor.BLUE).getFirst();
        assertEquals(0, island.size(PieceColor.RED));
        assertEquals(0, island.size(PieceColor.BLUE));
        assertEquals(0, island.size(PieceColor.PINK));

        assertThrows(EmptyContainerException.class,
                () -> island.remove());
    }

}
