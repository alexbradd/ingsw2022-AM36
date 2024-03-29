package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.Mage;
import it.polimi.ingsw.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
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
                .updateBoardOf(ann, b -> b.receiveDeck(Mage.MAGE, deck).playAssistant(AssistantType.CHEETAH));
        ap = new MockActionPhase(t, ann);
        m = new Messenger();
    }

    /**
     * Bound check doEffect()
     */
    @Test
    void boundCheckDoEffect() {
        assertThrows(IllegalArgumentException.class, () -> m.doEffect(null));
        assertThrows(IllegalArgumentException.class, () -> m.doEffect(ap, (CharacterStep[]) null));
        assertThrows(IllegalArgumentException.class, () -> m.doEffect(ap, (CharacterStep) null));
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Tuple<ActionPhase, Character> after = m.doEffect(ap);

        assertEquals(2, after.getFirst().getExtraMnMoves());
        assertEquals(CharacterType.MESSENGER.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(m, after.getSecond());
    }
}