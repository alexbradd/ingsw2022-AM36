package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for {@link Player} class.
 *
 * @author Mattia Busso
 * @see Player
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlayerTest {

    /**
     * The {@link Player} to test.
     */
    private Player player;

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
     * Initializes the other tests and tests the constructor.
     */
    @BeforeAll
    @DisplayName("Constructor test and initialization")
    @Test
    void initTest() {
        String playerUsername = "John";
        int entranceSize = 9;
        int numTowers = 9;
        towerColor = TowerColor.WHITE;

        player = new Player(playerUsername, entranceSize, numTowers, towerColor);

        initDeck();
        player = player.receiveDeck(new ArrayList<>(deck));

        assertThrows(IllegalArgumentException.class,
                () -> new Player(playerUsername, -1, -1, towerColor)
        );
    }

    /**
     * Test for receiveDeck() method.
     */
    @Test
    @DisplayName("receiveDeck() method test")
    void receiveDeckTest() {
        assertThrows(IllegalArgumentException.class, () -> player.receiveDeck(null));
        assertThrows(IllegalStateException.class, () -> player.receiveDeck(deck));
    }

    /**
     * Test for playAssistant() method.
     */
    @Test
    @DisplayName("playAssistant() method test")
    void playAssistantTest() {
        Random r = new Random();

        assertThrows(IllegalArgumentException.class, () -> player.playAssistant(null));

        while(deck.size() > 0) {
            int i = r.nextInt(0, deck.size());
            player = player.playAssistant(deck.get(i).getAssistantType());
            assertEquals(Optional.of(deck.get(i)), player.getLastPlayedAssistant());
            deck.remove(i);
        }

        assertThrows(NoSuchElementException.class, () -> player.playAssistant(AssistantType.CAT));
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
        assertThrows(IllegalStateException.class, () -> player.receiveTower(new Tower(towerColor, player)));
        assertThrows(IllegalArgumentException.class, () -> player.receiveTower(new Tower(TowerColor.BLACK, player)));
        assertThrows(IllegalArgumentException.class, () -> player.receiveTower(null));

        int lastNumTowers = player.getNumOfTowers();
        while(lastNumTowers != 0) {
            player = player.sendTower(t -> assertTrue(true));
            assertEquals(lastNumTowers - 1, player.getNumOfTowers());
            lastNumTowers--;
        }
        assertThrows(IllegalStateException.class, () -> player.sendTower(t -> assertAll()));
    }

    /**
     * Test for spendCoins() method.
     */
    @Test
    @DisplayName("spendCoins() method test")
    void spendCoinsTest() {
        assertThrows(IllegalStateException.class, () -> player.spendCoins(1));
        assertThrows(IllegalArgumentException.class, () -> player.spendCoins(0));
        assertThrows(IllegalArgumentException.class, () -> player.spendCoins(-1));
    }

    /**
     * Tests that the methods for hall and entrance updating throw exceptions when the update consumer is null
     * (see {@link HallTest} and {@link BoundedStudentContainerTest} for more testing on these entities).
     */
    @Test
    @DisplayName("Entrance and hall updating tests")
    void updateTest() {
        assertThrows(IllegalArgumentException.class,
                () -> player = player.updateEntrance(null));

        assertThrows(IllegalArgumentException.class,
                () -> player = player.updateHall(null));
    }

}
