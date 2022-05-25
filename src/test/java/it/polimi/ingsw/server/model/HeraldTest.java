package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Herald
 */
class HeraldTest {
    private static final PieceColor annColor = PieceColor.RED;
    private static Player ann;
    private static ActionPhase ap;
    private static Herald h;

    /**
     * Creates common state
     */
    @BeforeAll
    static void setup() {
        ann = new Player("ann");
        ArrayList<Assistant> deck = new ArrayList<>();
        deck.add(new Assistant(AssistantType.CHEETAH, Mage.MAGE));
        Table t = new Table()
                .addPlayer(ann, 7, 8, TowerColor.BLACK)
                .updateBoardOf(ann, b -> b
                        .receiveDeck(Mage.MAGE, deck)
                        .playAssistant(AssistantType.CHEETAH)
                        .receiveTower(new Tower(TowerColor.BLACK, ann)))
                .updateProfessors(ps -> {
                    ps.replaceAll(p -> {
                        if (p.getColor().equals(annColor))
                            return new Professor(annColor, ann);
                        return p;
                    });
                    return ps;
                })
                .updateIslandList(is -> {
                    Island i = is.remove(0);
                    i = i.updateStudents(c -> c.add(new Student(annColor)));
                    is.add(0, i);
                    return is;
                });
        ap = new MockActionPhase(t, ann);
        h = new Herald();
    }

    /**
     * Check that doEffect throws if passed null
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> h.doEffect(null));
        assertThrows(IllegalArgumentException.class, () -> h.doEffect(ap, (CharacterStep[]) null));
        assertThrows(IllegalArgumentException.class, () -> h.doEffect(ap, (CharacterStep) null));
    }

    /**
     * Bound check doEffect()
     */
    @Test
    void boundCheckDoEffect() {
        CharacterStep wrong1 = new CharacterStep();
        wrong1.setParameter("not island", "not number");
        CharacterStep wrong2 = new CharacterStep();
        wrong2.setParameter("island", "not number");
        CharacterStep wrong3 = new CharacterStep();
        wrong3.setParameter("island", "20");

        assertThrows(InvalidCharacterParameterException.class, () -> h.doEffect(ap, wrong1));
        assertThrows(InvalidCharacterParameterException.class, () -> h.doEffect(ap, wrong2));
        assertThrows(InvalidCharacterParameterException.class, () -> h.doEffect(ap, wrong3));
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        CharacterStep step = new CharacterStep();
        step.setParameter("island", "0");
        Tuple<ActionPhase, Character> after = h.doEffect(ap, step);

        assertTrue(after.getFirst().getTable().getIslandList().get(0).getControllingPlayer().isPresent());
        assertEquals(ann, after.getFirst().getTable().getIslandList().get(0).getControllingPlayer().get());
        assertTrue(after.getFirst().getTable().getIslandList().get(0).getConqueringColor().isPresent());
        assertEquals(TowerColor.BLACK, after.getFirst().getTable().getIslandList().get(0).getConqueringColor().get());
        assertEquals(CharacterType.HERALD.getInitialCost() + 1, after.getSecond().getCost());


        step = new CharacterStep();
        step.setParameter("island", "2");
        after = h.doEffect(ap, step);
        assertFalse(after.getFirst().getTable().getIslandList().get(0).getControllingPlayer().isPresent());
        assertFalse(after.getFirst().getTable().getIslandList().get(0).getConqueringColor().isPresent());
    }

    /**
     * Check that doEffect() ignores any exceeding steps
     */
    @Test
    void doEffect_withExceedingSteps() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        CharacterStep step1 = new CharacterStep();
        step1.setParameter("island", "0");
        CharacterStep step2 = new CharacterStep();
        step2.setParameter("island", "2");
        Tuple<ActionPhase, Character> after = h.doEffect(ap, step1, step2);

        assertTrue(after.getFirst().getTable().getIslandList().get(0).getControllingPlayer().isPresent());
        assertEquals(ann, after.getFirst().getTable().getIslandList().get(0).getControllingPlayer().get());
        assertTrue(after.getFirst().getTable().getIslandList().get(0).getConqueringColor().isPresent());
        assertEquals(TowerColor.BLACK, after.getFirst().getTable().getIslandList().get(0).getConqueringColor().get());
    }
}