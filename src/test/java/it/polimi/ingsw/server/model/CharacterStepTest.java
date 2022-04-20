package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for CharacterStep.
 */
class CharacterStepTest {
    private CharacterStep cs;

    /**
     * Setup fresh objects for each test
     */
    @BeforeEach
    void setUp() {
        cs = new CharacterStep();
    }

    /**
     * Null check methods
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> cs.setParameter(null, null));
        assertThrows(IllegalArgumentException.class, () -> cs.setParameter("a", null));
    }

    /**
     * Checks {@link CharacterStep#getParameterAsIslandIndex(String, ActionPhase)} bounds and correctness of
     * operation.
     */
    @Test
    void getParameterAsIslandIndex() throws InvalidCharacterParameterException {
        Player ann = new Player("ann");
        ArrayList<Assistant> deck = new ArrayList<>();
        deck.add(new Assistant(AssistantType.CHEETAH, Mage.MAGE));
        Table t = new Table()
                .addPlayer(ann, 7, 8, TowerColor.BLACK)
                .updateBoardOf(ann, b -> b.receiveDeck(deck).playAssistant(AssistantType.CHEETAH));
        ActionPhase p = new MockActionPhase(t, ann);

        cs.setParameter("a", "a");
        assertThrows(InvalidCharacterParameterException.class, () -> cs.getParameterAsIslandIndex("a", p));
        cs.setParameter("a", "-1");
        assertThrows(InvalidCharacterParameterException.class, () -> cs.getParameterAsIslandIndex("a", p));
        cs.setParameter("a", "20");
        assertThrows(InvalidCharacterParameterException.class, () -> cs.getParameterAsIslandIndex("a", p));
        cs.setParameter("a", "1");
        assertEquals(1, cs.getParameterAsIslandIndex("a", p));
    }

    /**
     * Checks {@link CharacterStep#getParameterAsColor(String)} bounds and correctness of operation.
     */
    @Test
    void getParameterAsColor() throws InvalidCharacterParameterException {
        cs.setParameter("a", "not a color");
        assertThrows(InvalidCharacterParameterException.class, () -> cs.getParameterAsColor("a"));
        cs.setParameter("a", "RED");
        assertEquals(PieceColor.RED, cs.getParameterAsColor("a"));
    }
}