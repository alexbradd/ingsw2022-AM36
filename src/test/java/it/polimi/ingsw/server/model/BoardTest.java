package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.Mage;
import it.polimi.ingsw.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.NoTowersException;
import it.polimi.ingsw.server.model.exceptions.NotEnoughCoinsException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link Board} class.
 *
 * @author Mattia Busso, Leonardo Bianconi
 * @see Board
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BoardTest {

    /**
     * The {@link Player} who owns the {@link Board} to test;.
     */
    private Player player;

    /**
     * The {@link Board} to be tested.
     */
    private Board board;

    /**
     * A deck of assistants to give the {@code Player}.
     *
     * @see Assistant
     */
    private List<Assistant> deck;

    /**
     * The {@link TowerColor} of the player.
     */
    private TowerColor towerColor;

    /**
     * The maximum number of towers in the player's board.
     */
    private int numTowers;
    /**
     * Initializes the other tests and tests the constructor.
     */
    @BeforeAll
    @DisplayName("Constructor test and initialization")
    @Test
    void initTest() {
        String playerUsername = "John";
        int entranceSize = 9;
        numTowers = 9;
        towerColor = TowerColor.WHITE;

        player = new Player(playerUsername);
        board = new Board(player, entranceSize, numTowers, towerColor);


        initDeck();
        board = board.receiveDeck(Mage.MAGE, new ArrayList<>(deck));

        assertThrows(IllegalArgumentException.class,
                () -> new Board(player, -1, -1, towerColor)
        );
    }

    /**
     * Test for receiveDeck() method.
     */
    @Test
    @DisplayName("receiveDeck() method test")
    void receiveDeckTest() {
        assertThrows(IllegalArgumentException.class, () -> board.receiveDeck(null, null));
        assertThrows(IllegalArgumentException.class, () -> board.receiveDeck(null, deck));
        assertThrows(IllegalStateException.class, () -> board.receiveDeck(Mage.FAIRY, deck));
    }

    /**
     * Test for playAssistant() method.
     */
    @Test
    @DisplayName("playAssistant() method test")
    void playAssistantTest() {
        Random r = new Random();

        assertThrows(IllegalArgumentException.class, () -> board.playAssistant(null));

        while(deck.size() > 0) {
            int i = r.nextInt(0, deck.size());
            board = board.playAssistant(deck.get(i).getAssistantType());
            assertEquals(Optional.of(deck.get(i)), board.getLastPlayedAssistant());
            deck.remove(i);
        }

        assertThrows(NoSuchElementException.class, () -> board.playAssistant(AssistantType.CAT));
    }

    /**
     * Deck's setup.
     */
    private void initDeck() {
        deck = new ArrayList<>();

        deck.add(new Assistant(AssistantType.CHEETAH, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.OSTRICH, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.CAT, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.EAGLE, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.FOX, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.SNAKE, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.OCTOPUS, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.DOG, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.ELEPHANT, Mage.FAIRY));
        deck.add(new Assistant(AssistantType.TURTLE, Mage.FAIRY));
    }

    /**
     * Test for the receiveTower() and sendTower() methods.
     */
    @Test
    @DisplayName("receiveTower() and sendTower() methods test")
    void towersFlowTest() {
        assertThrows(IllegalArgumentException.class, () -> board.receiveTower(new Tower(TowerColor.BLACK, player)));
        assertThrows(IllegalArgumentException.class, () -> board.receiveTower(null));

        for (int i = 0; i < numTowers; i++)
            board = board.receiveTower(new Tower(towerColor, player));
        assertThrows(IllegalStateException.class, () -> board.receiveTower(new Tower(towerColor, player)));

        int lastNumTowers = board.getNumOfTowers();
        while(lastNumTowers != 0) {
            Tuple<Board, Tower> t = board.sendTower();
            board = t.getFirst();
            assertEquals(lastNumTowers - 1, board.getNumOfTowers());
            lastNumTowers--;
        }
        assertThrows(NoTowersException.class, () -> board.sendTower());
    }

    /**
     * Test for spendCoins() method.
     */
    @Test
    @DisplayName("spendCoins() method test")
    void spendCoinsTest() {
        assertThrows(NotEnoughCoinsException.class, () -> board.spendCoins(1));
        assertThrows(IllegalArgumentException.class, () -> board.spendCoins(0));
        assertThrows(IllegalArgumentException.class, () -> board.spendCoins(-1));
    }

    /**
     * Tests that the methods for hall and entrance updating throw exceptions when the update consumer is null
     * (see {@link HallTest} and {@link BoundedStudentContainerTest} for more testing on these entities).
     */
    @Test
    @DisplayName("Entrance and hall updating tests")
    void updateTest() {
        assertThrows(IllegalArgumentException.class,
                () -> board = board.updateEntrance(null));

        assertThrows(IllegalArgumentException.class,
                () -> board = board.updateHall(null));
    }

}
