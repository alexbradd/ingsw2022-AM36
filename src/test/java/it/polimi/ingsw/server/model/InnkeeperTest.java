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
 * Test class for Innkeeper
 */
class InnkeeperTest {
    private static ActionPhase ap;
    private static Innkeeper i;

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
        i = new Innkeeper();
    }

    /**
     * Bound check doEffect()
     */
    @Test
    void boundCheckDoEffect() {
        assertThrows(IllegalArgumentException.class, () -> i.doEffect(ap, null));
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException {
        Tuple<ActionPhase, Character> after = i.doEffect(ap, new CharacterStep[]{});

        assertNotEquals(ap.getMaxExtractor(), after.getFirst().getMaxExtractor());
        assertInstanceOf(EqualityInclusiveMaxExtractor.class, after.getFirst().getMaxExtractor());
        assertEquals(CharacterType.INNKEEPER.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(i, after.getSecond());
    }
}