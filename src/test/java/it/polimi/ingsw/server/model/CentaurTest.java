package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Centaur
 */
class CentaurTest {
    private static ActionPhase ap;
    private static InfluenceDecoratingCharacter c;

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
        c = new InfluenceDecoratingCharacter(InfluenceDecoratingCharacter.Behaviour.CENTAUR);
    }

    /**
     * Bound check doEffect()
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> c.doEffect(null));
        assertThrows(IllegalArgumentException.class, () -> c.doEffect(ap, (CharacterStep[]) null));
        assertThrows(IllegalArgumentException.class, () -> c.doEffect(ap, (CharacterStep) null));
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Tuple<ActionPhase, Character> after = c.doEffect(ap);

        assertNotEquals(ap.getInfluenceCalculator(), after.getFirst().getInfluenceCalculator());
        assertInstanceOf(IgnoreTowersInfluenceDecorator.class, after.getFirst().getInfluenceCalculator());
        assertEquals(CharacterType.CENTAUR.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(c, after.getSecond());
    }
}