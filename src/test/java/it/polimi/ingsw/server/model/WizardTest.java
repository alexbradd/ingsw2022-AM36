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
 * Test class for Wizard
 */
class WizardTest {
    private static ActionPhase ap;
    private static InfluenceDecoratingCharacter w;

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
        w = new InfluenceDecoratingCharacter(InfluenceDecoratingCharacter.Behaviour.WIZARD);
    }

    /**
     * Bound check doEffect()
     */
    @Test
    void boundCheckDoEffect() {
        CharacterStep wrong1 = new CharacterStep();
        wrong1.setParameter("not color", "not color");
        CharacterStep wrong2 = new CharacterStep();
        wrong2.setParameter("color", "not color");

        assertThrows(IllegalArgumentException.class, () -> w.doEffect(ap, null));
        assertThrows(InvalidCharacterParameterException.class, () -> w.doEffect(ap, new CharacterStep[]{}));
        assertThrows(InvalidCharacterParameterException.class, () -> w.doEffect(ap, new CharacterStep[]{wrong1}));
        assertThrows(InvalidCharacterParameterException.class, () -> w.doEffect(ap, new CharacterStep[]{wrong2}));
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException {
        CharacterStep step = new CharacterStep();
        step.setParameter("color", "RED");
        Tuple<ActionPhase, Character> after = w.doEffect(ap, new CharacterStep[]{step});

        assertInstanceOf(RemoveStudentInfluenceDecorator.class, after.getFirst().getInfluenceCalculator());
        assertEquals(CharacterType.WIZARD.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(w, after.getSecond());
    }
}