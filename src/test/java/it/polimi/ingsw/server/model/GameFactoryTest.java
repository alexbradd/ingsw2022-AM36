package it.polimi.ingsw.server.model;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

/**
 * Unit test for the GameFactory class.
 * @author Leonardo Bianconi
 * @see GameFactory
 */
public class GameFactoryTest {
    /**
     * Creates a new two player game, checks that the newly created Game instance is not a null pointer.
     */
    @Test
    public void checkGameNotNullTest()
    {
        Game g = GameFactory.twoPlayerGame(false);
        assertNotEquals(g, null);
    }

    /**
     *
     */
    @Test
    public void correctNumPlayersTest() {

    }
}

