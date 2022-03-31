package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        player.receiveDeck(deck);

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

        assertThrows(IndexOutOfBoundsException.class, () -> player.playAssistant(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> player.playAssistant(deck.size()));

        while(deck.size() > 0) {
            int i = r.nextInt(deck.size());
            player.playAssistant(i);
            assertEquals(Optional.of(deck.get(i)), player.getLastPlayedAssistant());
            deck.remove(i);
        }

        assertThrows(IndexOutOfBoundsException.class, () -> player.playAssistant(0));
    }

    /**
     * Deck's setup.
     */
    private void initDeck() {
        deck = new ArrayList<>();
        for(int i = 0; i < 10; i++) deck.add(new Assistant(1, 1, Mage.FAIRY));
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
            player.sendTower();
            assertEquals(lastNumTowers - 1, player.getNumOfTowers());
            lastNumTowers--;
        }
        assertThrows(IllegalStateException.class, () -> player.sendTower());
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

}
