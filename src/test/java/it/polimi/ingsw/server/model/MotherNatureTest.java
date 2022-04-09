package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MotherNature
 */
class MotherNatureTest {
    private MotherNature mn;
    private IslandList list;
    private static Player player1, player2;
    private static Professor professor1, professor2;

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
    }

    /**
     * Creates a new IslandList and MotherNature for each test
     */
    @BeforeEach
    void setUp() {
        list = new IslandList();
        mn = new MotherNature(list, 0);
    }

    /**
     * Checks that all methods correctly complain when passed null
     */
    @Test
    void withNull() {
        assertThrows(IllegalArgumentException.class, () -> mn.setCalculator(null));
        assertThrows(IllegalArgumentException.class, () -> mn.setExtractor(null));
        assertThrows(IllegalArgumentException.class, () -> mn.assignTower(null));
    }

    /**
     * Bound check
     */
    @Test
    void boundCheck() {
        assertThrows(IllegalArgumentException.class, () -> new MotherNature(null, 0));
        assertThrows(IllegalArgumentException.class, () -> new MotherNature(list, -1));
        assertThrows(IllegalArgumentException.class, () -> new MotherNature(list, 20));
        assertThrows(IllegalArgumentException.class, () -> mn.move(null, 0));
        assertThrows(IllegalArgumentException.class, () -> mn.move(list, 0));
        assertThrows(IllegalArgumentException.class, () -> mn.move(null, -15));
        assertThrows(IllegalArgumentException.class, () -> mn.move(list, -15));
    }

    /**
     * Check if mother nature correctly assigns towers on stop
     */
    @Test
    void movement() {
        int player1NumOfTowers = player1.getNumOfTowers();
        int player2NumOfTowers = player2.getNumOfTowers();

        list.get(0).receiveStudent(new Student(professor1));
        list.get(0).receiveStudent(new Student(professor1));
        list.get(0).receiveStudent(new Student(professor1));
        list.get(0).receiveStudent(new Student(professor2));
        list.get(0).receiveStudent(new Student(professor2));

        for (int i = 0; i < list.size(); i++)
            mn.move(list, 1);

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

        list.get(0).receiveStudent(new Student(professor1));
        list.get(0).receiveStudent(new Student(professor1));
        list.get(0).receiveStudent(new Student(professor1));
        list.get(0).receiveStudent(new Student(professor2));
        list.get(0).receiveStudent(new Student(professor2));

        mn.assignTower(list.get(2));

        assertEquals(player1.getNumOfTowers(), player1NumOfTowers);
        assertEquals(player2.getNumOfTowers(), player2NumOfTowers);
    }
}