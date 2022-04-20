package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Messenger
 */
class MessengerTest {
    private static ActionPhase ap;
    private static Messenger m;

    /**
     * Creates common state
     */
    @BeforeAll
    static void setup() {
        Player ann = new Player("ann");
        ArrayList<Assistant> deck = new ArrayList<>();
        deck.add(new Assistant(AssistantType.CHEETAH, Mage.MAGE));
        Table t = new Table()
                .addPlayer(ann, 7, 8, TowerColor.BLACK)
                .updateBoardOf(ann, b -> b.receiveDeck(deck).playAssistant(AssistantType.CHEETAH));
        ap = new MockActionPhase(t, ann);
        m = new Messenger();
    }

    /**
     * Bound check doEffect()
     */
    @Test
    void boundCheckDoEffect() {
        assertThrows(IllegalArgumentException.class, () -> m.doEffect(ap, null));
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException {
        Tuple<ActionPhase, Character> after = m.doEffect(ap, new CharacterStep[]{});

        assertEquals(2, after.getFirst().getExtraMnMoves());
        assertEquals(CharacterType.MESSENGER.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(m, after.getSecond());
    }
}