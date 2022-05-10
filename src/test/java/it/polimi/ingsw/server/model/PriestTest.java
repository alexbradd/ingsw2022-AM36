package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Priest card
 */
class PriestTest {
    private static final Player ann = new Player("ann");
    private static PreparePhase pp;
    private static ActionPhase ap;
    private static PriestAndPrincess c;
    private static Table t;

    /**
     * Creates common state
     */
    @BeforeAll
    static void setup() {
        ArrayList<Assistant> deck = new ArrayList<>();
        deck.add(new Assistant(AssistantType.CHEETAH, Mage.MAGE));
        t = new Table()
                .addPlayer(ann, 7, 8, TowerColor.BLACK)
                .updateBoardOf(ann, b -> b.receiveDeck(Mage.MAGE, deck).playAssistant(AssistantType.CHEETAH));
        ap = new MockActionPhase(t, ann);
        c = new PriestAndPrincess(PriestAndPrincess.Behaviour.PRIEST);
        pp = new MockPreparePhase();
    }

    /**
     * Check doPrepare hook
     */
    @Test
    void doPrepare() {
        assertEquals(0, c.getStudents().size());
        PriestAndPrincess after = (PriestAndPrincess) c.doPrepare(pp).getSecond();
        assertEquals(4, after.getStudents().size());
        assertNotSame(after, c);
    }

    /**
     * Bound check doEffect()
     */
    @ParameterizedTest
    @MethodSource("boundCheckSource")
    void boundCheckDoEffect(StepTest t) {
        PriestAndPrincess p = (PriestAndPrincess) c.add(new Student(PieceColor.RED));
        CharacterStep wrong = new CharacterStep();
        wrong.setParameter(t.cardKey, t.cardValue);
        wrong.setParameter(t.islandKey, t.islandValue);

        assertThrows(IllegalArgumentException.class, () -> p.doEffect(ap, null));
        assertThrows(InvalidCharacterParameterException.class, () -> p.doEffect(ap, new CharacterStep[]{}));
        assertThrows(InvalidCharacterParameterException.class, () -> p.doEffect(ap, new CharacterStep[]{wrong}));
    }

    /**
     * Generates test cases for {@link #boundCheckDoEffect(StepTest)}.
     *
     * @return a stream of StepTest
     */
    static Stream<StepTest> boundCheckSource() {
        return Stream.of(
                new StepTest("wrong", "wrong", "not color", "-1"),
                new StepTest("card", "wrong", "not color", "-1"),
                new StepTest("wrong", "island", "not color", "-1"),
                new StepTest("card", "island", "not color", "-1"),
                new StepTest("card", "island", "RED", "-1"),
                new StepTest("card", "island", "PINK", "0")
        );
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException {
        Table withSack = t.updateSack(s -> s.add(new Student(PieceColor.RED)));
        PriestAndPrincess preUpdate = (PriestAndPrincess) c.doPrepare(pp).getSecond();
        CharacterStep step = new CharacterStep();
        step.setParameter("card", preUpdate.getStudents().stream().findAny().orElseThrow().getColor().toString());
        step.setParameter("island", "0");
        ActionPhase actionPhase = new MockActionPhase(withSack, ann);
        Tuple<ActionPhase, Character> after = preUpdate.doEffect(actionPhase, new CharacterStep[]{step});

        assertEquals(1, after.getFirst().getTable().getIslandList().get(0).getStudents().size());
        assertEquals(4, ((StudentStoreCharacter) after.getSecond()).getStudents().size());
        assertEquals(CharacterType.PRIEST.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(preUpdate, after.getSecond());
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way if the sack is empty
     */
    @Test
    void doEffectWithEmptySack() throws InvalidCharacterParameterException {
        PriestAndPrincess preUpdate = (PriestAndPrincess) c.doPrepare(pp).getSecond();
        CharacterStep step = new CharacterStep();
        step.setParameter("card", preUpdate.getStudents().stream().findAny().orElseThrow().getColor().toString());
        step.setParameter("island", "0");
        Tuple<ActionPhase, Character> after = preUpdate.doEffect(ap, new CharacterStep[]{step});

        assertEquals(1, after.getFirst().getTable().getIslandList().get(0).getStudents().size());
        assertEquals(3, ((StudentStoreCharacter) after.getSecond()).getStudents().size());
        assertEquals(CharacterType.PRIEST.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(preUpdate, after.getSecond());
    }

    /**
     * Simple data holder used by {@link #boundCheckDoEffect(StepTest)} and {@link #boundCheckSource()}.
     */
    private static class StepTest {
        public String cardKey;
        public String islandKey;
        public String cardValue;
        public String islandValue;

        public StepTest(String cardKey, String islandKey, String cardValue, String islandValue) {
            this.cardKey = cardKey;
            this.islandKey = islandKey;
            this.cardValue = cardValue;
            this.islandValue = islandValue;
        }
    }
}