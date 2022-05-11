package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Princess card
 */
class PrincessTest {
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
        c = new PriestAndPrincess(PriestAndPrincess.Behaviour.PRINCESS);
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
     * Check that doEffect throws if passed null
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> c.doEffect(null));
        assertThrows(IllegalArgumentException.class, () -> c.doEffect(ap, (CharacterStep[]) null));
        assertThrows(IllegalArgumentException.class, () -> c.doEffect(ap, (CharacterStep) null));
    }

    /**
     * Checks that doEffect parses steps correctly
     */
    @ParameterizedTest
    @MethodSource("parseCheckSource")
    void parseCheck(StepTest t) {
        PriestAndPrincess p = (PriestAndPrincess) c.add(new Student(PieceColor.RED));
        CharacterStep wrong = new CharacterStep();
        wrong.setParameter(t.cardKey, t.cardValue);

        assertThrows(InvalidCharacterParameterException.class, () -> p.doEffect(ap));
        assertThrows(InvalidCharacterParameterException.class, () -> p.doEffect(ap, wrong));
    }

    /**
     * Generates test cases for {@link #parseCheck(StepTest)}.
     *
     * @return a stream of StepTest
     */
    static Stream<StepTest> parseCheckSource() {
        return Stream.of(
                new StepTest("wrong", "not a color"),
                new StepTest("card", "not a color")
        );
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Table withSack = t.updateSack(s -> s.add(new Student(PieceColor.RED)));
        PriestAndPrincess preUpdate = (PriestAndPrincess) c.doPrepare(pp).getSecond();
        CharacterStep step = new CharacterStep();
        step.setParameter("card", preUpdate.getStudents().stream().findAny().orElseThrow().getColor().toString());
        ActionPhase actionPhase = new MockActionPhase(withSack, ann);
        Tuple<ActionPhase, Character> after = preUpdate.doEffect(actionPhase, step);

        assertEquals(1, after.getFirst().getTable().getBoardOf(ann).getHall().size());
        assertTrue(after.getFirst()
                .getTable()
                .getProfessors().stream()
                .anyMatch(p -> Objects.equals(p.getOwner(), Optional.of(ann))));
        assertEquals(4, ((StudentStoreCharacter) after.getSecond()).getStudents().size());
        assertEquals(CharacterType.PRINCESS.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(preUpdate, after.getSecond());
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way if the sack is empty
     */
    @Test
    void doEffect_emptySack() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        PriestAndPrincess preUpdate = (PriestAndPrincess) c.doPrepare(pp).getSecond();
        CharacterStep step = new CharacterStep();
        step.setParameter("card", preUpdate.getStudents().stream().findAny().orElseThrow().getColor().toString());
        Tuple<ActionPhase, Character> after = preUpdate.doEffect(ap, step);

        assertEquals(1, after.getFirst().getTable().getBoardOf(ann).getHall().size());
        assertTrue(after.getFirst()
                .getTable()
                .getProfessors().stream()
                .anyMatch(p -> Objects.equals(p.getOwner(), Optional.of(ann))));
        assertEquals(3, ((StudentStoreCharacter) after.getSecond()).getStudents().size());
        assertEquals(CharacterType.PRINCESS.getInitialCost() + 1, after.getSecond().getCost());
        assertNotSame(ap, after.getFirst());
        assertNotSame(preUpdate, after.getSecond());
    }

    /**
     * Check that doEffect() explodes if trying to put Students in a full Hall
     */
    @Test
    void doEffect_fullHall() {
        PriestAndPrincess preUpdate = (PriestAndPrincess) c.doPrepare(pp).getSecond();
        PieceColor color = preUpdate.getStudents().stream().findAny().orElseThrow().getColor();
        Table withFullHall = t.updateBoardOf(ann, b -> b.updateHall(h -> {
            while (!h.isFull(color))
                h = h.add(new Student(color));
            return h;
        }));
        CharacterStep step = new CharacterStep();
        step.setParameter("card", color.toString());
        ActionPhase actionPhase = new MockActionPhase(withFullHall, ann);
        assertThrows(InvalidPhaseUpdateException.class, () -> preUpdate.doEffect(actionPhase, step));
    }

    /**
     * Check that doEffect() throws exception if invoked with a color that is not present on the card
     */
    @Test
    void doEffect_emptyCard() {
        Table withSack = t.updateSack(s -> s.add(new Student(PieceColor.RED)));
        PriestAndPrincess preUpdate = (PriestAndPrincess) c.add(new Student(PieceColor.RED));
        CharacterStep step = new CharacterStep();
        step.setParameter("card", "PINK");
        step.setParameter("island", "0");
        ActionPhase actionPhase = new MockActionPhase(withSack, ann);
        assertThrows(InvalidPhaseUpdateException.class, () -> preUpdate.doEffect(actionPhase, step));
    }

    /**
     * Check that doEffect() ignores exceeding steps
     */
    @Test
    void doEffect_exceedingSteps() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Table withSack = t.updateSack(s -> s.add(new Student(PieceColor.RED)));
        PriestAndPrincess preUpdate = (PriestAndPrincess) c.doPrepare(pp).getSecond();
        CharacterStep step = new CharacterStep();
        step.setParameter("card", preUpdate.getStudents().stream().findAny().orElseThrow().getColor().toString());
        ActionPhase actionPhase = new MockActionPhase(withSack, ann);
        Tuple<ActionPhase, Character> after = preUpdate.doEffect(actionPhase, step, step);

        assertEquals(1, after.getFirst().getTable().getBoardOf(ann).getHall().size());
        assertTrue(after.getFirst()
                .getTable()
                .getProfessors().stream()
                .anyMatch(p -> Objects.equals(p.getOwner(), Optional.of(ann))));
        assertEquals(4, ((StudentStoreCharacter) after.getSecond()).getStudents().size());
    }

    /**
     * Simple data holder used by {@link #parseCheck(StepTest)} and {@link #parseCheckSource()}.
     */
    private static class StepTest {
        public String cardKey;
        public String cardValue;

        public StepTest(String cardKey, String cardValue) {
            this.cardKey = cardKey;
            this.cardValue = cardValue;
        }
    }
}
