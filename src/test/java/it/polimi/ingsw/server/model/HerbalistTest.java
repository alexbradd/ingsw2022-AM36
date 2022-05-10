package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.iterators.AssistantValueIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Herbalist
 */
public class HerbalistTest {
    private static PreparePhase pp;
    private static ActionPhase ap;
    private static Herbalist h;

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
        pp = new MockPreparePhase();
        h = new Herbalist();
    }

    /**
     * Check doPrepare hook
     */
    @Test
    void doPrepare() {
        assertEquals(0, h.getNumOfBlocks());
        Herbalist after = (Herbalist) h.doPrepare(pp).getSecond();
        assertEquals(4, after.getNumOfBlocks());
        assertNotSame(after, h);
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

        assertThrows(IllegalArgumentException.class, () -> h.doEffect(ap, null));
        assertThrows(InvalidCharacterParameterException.class, () -> h.doEffect(ap, new CharacterStep[]{}));
        assertThrows(InvalidCharacterParameterException.class, () -> h.doEffect(ap, new CharacterStep[]{wrong1}));
        assertThrows(InvalidCharacterParameterException.class, () -> h.doEffect(ap, new CharacterStep[]{wrong2}));
        assertThrows(InvalidCharacterParameterException.class, () -> h.doEffect(ap, new CharacterStep[]{wrong3}));
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException {
        CharacterStep step = new CharacterStep();
        step.setParameter("island", "0");
        Character preUpdate = h.doPrepare(pp).getSecond();
        Tuple<ActionPhase, Character> after = preUpdate.doEffect(ap, new CharacterStep[]{step});

        assertTrue(after.getFirst().getTable().getIslandList().get(0).isBlocked());
        assertEquals(3, after.getSecond().getNumOfBlocks());
        assertEquals(CharacterType.HERBALIST.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(preUpdate, after.getSecond());
    }
}
