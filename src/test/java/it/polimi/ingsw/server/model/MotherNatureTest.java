package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MotherNature
 */
class MotherNatureTest {
    private MotherNature mn;
    private IslandList list;
    private static Player player1, player2;
    private static Professor professor1, professor2;
    private static List<Professor> professorList;

    /**
     * Sets up static fields
     */
    @BeforeAll
    static void staticSetUp() {
        player1 = new Player("Napoleon", 1, 10, TowerColor.WHITE);
        player2 = new Player("Cesar", 1, 10, TowerColor.BLACK);
        professor1 = new Professor(PieceColor.RED);
        professor2 = new Professor(PieceColor.BLUE);
        professor1.assign(player1);
        professor2.assign(player2);

        professorList = new ArrayList<>();
        professorList.add(professor1);
        professorList.add(professor2);
        professorList.add(new Professor(PieceColor.YELLOW));
        professorList.add(new Professor(PieceColor.PINK));
        professorList.add(new Professor(PieceColor.GREEN));
    }

    /**
     * Creates a new IslandList and MotherNature for each test
     */
    @BeforeEach
    void setUp() {
        list = new IslandList();
        mn = new MotherNature(list);
    }

    /**
     * Checks that all methods correctly complain when passed null
     */
    @Test
    void withNull() {
        assertThrows(IllegalArgumentException.class, () -> mn.setCalculator(null));
        assertThrows(IllegalArgumentException.class, () -> mn.setExtractor(null));
        assertThrows(IllegalArgumentException.class, () -> mn.assignTower(null, null));
    }

    /**
     * Bound check movement
     */
    @Test
    void boundCheckMovement() {
        assertThrows(IllegalArgumentException.class, () -> mn.move(0, professorList));
        assertThrows(IllegalArgumentException.class, () -> mn.move(-15, professorList));
    }

    /**
     * Check if mother nature correctly assigns towers on stop
     */
    @Test
    void movement() {
        int player1NumOfTowers = player1.getNumOfTowers();
        int player2NumOfTowers = player2.getNumOfTowers();

        list.get(0).receiveStudent(new Student(professor1.getColor()));
        list.get(0).receiveStudent(new Student(professor1.getColor()));
        list.get(0).receiveStudent(new Student(professor1.getColor()));
        list.get(0).receiveStudent(new Student(professor2.getColor()));
        list.get(0).receiveStudent(new Student(professor2.getColor()));

        for (int i = 0; i < list.size(); i++)
            mn.move(1, professorList);

        assertEquals(player1.getNumOfTowers(), player1NumOfTowers - 1);
        assertEquals(player2.getNumOfTowers(), player2NumOfTowers);
    }

    /**
     * Check is calculation is correctly done on demand
     */
    @Test
    void randomTowerAssignment() {
        int player1NumOfTowers = player1.getNumOfTowers();
        int player2NumOfTowers = player2.getNumOfTowers();

        list.get(0).receiveStudent(new Student(professor1.getColor()));
        list.get(0).receiveStudent(new Student(professor1.getColor()));
        list.get(0).receiveStudent(new Student(professor1.getColor()));
        list.get(0).receiveStudent(new Student(professor2.getColor()));
        list.get(0).receiveStudent(new Student(professor2.getColor()));

        mn.assignTower(list.get(2), professorList);

        assertEquals(player1.getNumOfTowers(), player1NumOfTowers);
        assertEquals(player2.getNumOfTowers(), player2NumOfTowers);
    }
}