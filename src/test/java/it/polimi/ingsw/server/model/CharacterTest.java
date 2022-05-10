package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for Character. Since Character is abstract, a MockCharacter is used.
 *
 * @see MockCharacter
 */
class CharacterTest {
    private static ActionPhase ap;
    private MockCharacter mc;

    /**
     * Sets up shred ActionPhase
     */
    @BeforeAll
    static void staticSetUp() {
        Player ann = new Player("ann");
        ArrayList<Assistant> deck = new ArrayList<>();
        deck.add(new Assistant(AssistantType.CHEETAH, Mage.MAGE));
        Table t = new Table()
                .addPlayer(ann, 7, 8, TowerColor.BLACK)
                .updateBoardOf(ann, b -> b.receiveDeck(Mage.MAGE, deck).playAssistant(AssistantType.CHEETAH));
        ap = new MockActionPhase(t, ann);
    }

    /**
     * Create a fresh character after each test
     */
    @BeforeEach
    void setUp() {
        mc = new MockCharacter(CharacterType.HERBALIST);
    }

    /**
     * Null check concrete methods
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> new MockCharacter(null));
        assertThrows(IllegalArgumentException.class, () -> mc.doEffect(null, null));
        assertThrows(IllegalArgumentException.class, () -> mc.doEffect(ap, null));
        assertThrows(IllegalArgumentException.class, () -> mc.doPrepare(null));
    }

    /**
     * Check that all "optional" methods throw exceptions
     */
    @Test
    void testOptionalMethods() {
        assertThrows(UnsupportedOperationException.class, () -> mc.pushBlock(null));
        assertThrows(UnsupportedOperationException.class, () -> mc.popBlock());
    }

    /**
     * Test that the default doPrepare() does nothing
     */
    @Test
    void testDoPrepare() {
        PreparePhase pp = new MockPreparePhase();
        Tuple<PreparePhase, Character> after = mc.doPrepare(pp);
        assertEquals(pp, after.getFirst());
        assertEquals(mc, after.getSecond());
    }

    /**
     * Test that calling doEffect increases cost by 1
     */
    @Test
    void testDoEffect() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        assertEquals(CharacterType.HERBALIST.getInitialCost(), mc.getCost());
        mc = (MockCharacter) mc.doEffect(ap, new CharacterStep[]{}).getSecond();
        assertEquals(CharacterType.HERBALIST.getInitialCost() + 1, mc.getCost());
    }

    /**
     * Mock Character card. It does nothing.
     */
    private static class MockCharacter extends Character {
        public CharacterType type;

        /**
         * {@inheritDoc}
         */
        MockCharacter(CharacterType type) {
            super(type);
            this.type = type;
        }

        /**
         * Abstract method that returns a shallow copy of the current object.
         *
         * @return returns a shallow copy of the current object.
         */
        @Override
        Character shallowCopy() {
            return new MockCharacter(type);
        }
    }
}