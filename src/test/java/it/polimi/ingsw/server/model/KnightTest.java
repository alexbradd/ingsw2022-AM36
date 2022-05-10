package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.iterators.AssistantValueIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Knight
 */
class KnightTest {
    private static ActionPhase ap;
    private static InfluenceDecoratingCharacter k;

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
        AssistantValueIterator avi = new AssistantValueIterator(t.getBoards(), 0);
        ap = new MockActionPhase(t, ann);
        k = new InfluenceDecoratingCharacter(InfluenceDecoratingCharacter.Behaviour.KNIGHT);
    }

    /**
     * Bound check doEffect()
     */
    @Test
    void boundCheckDoEffect() {
        assertThrows(IllegalArgumentException.class, () -> k.doEffect(ap, null));
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Tuple<ActionPhase, Character> after = k.doEffect(ap, new CharacterStep[]{});

        assertNotEquals(ap.getInfluenceCalculator(), after.getFirst().getInfluenceCalculator());
        assertInstanceOf(ExtraPointsInfluenceDecorator.class, after.getFirst().getInfluenceCalculator());
        assertEquals(CharacterType.KNIGHT.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(k, after.getSecond());
    }
}