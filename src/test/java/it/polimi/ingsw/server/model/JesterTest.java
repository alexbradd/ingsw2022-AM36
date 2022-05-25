package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the Jester card
 */
class JesterTest {
    private static Player ann;
    private static ActionPhase ap;
    private static Jester j;

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
                        .updateEntrance(c -> c.add(new Student(PieceColor.BLUE))));
        ap = new MockActionPhase(t, ann);
        j = new Jester();
    }

    /**
     * Check that doEffect throws if passed null
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> j.doEffect(null));
        assertThrows(IllegalArgumentException.class, () -> j.doEffect(ap, (CharacterStep[]) null));
        assertThrows(IllegalArgumentException.class, () -> j.doEffect(ap, (CharacterStep) null));
    }

    /**
     * Test that doEffect() parses steps correctly
     */
    @ParameterizedTest
    @MethodSource("parseCheckSource")
    void parseCheck(StepTest t) {
        CharacterStep wrong = new CharacterStep();
        wrong.setParameter(t.cardKey, t.cardValue);
        wrong.setParameter(t.entranceKey, t.entranceValue);
        assertThrows(InvalidCharacterParameterException.class, () -> j.doEffect(ap, wrong));
    }

    /**
     * Generates test cases for {@link #parseCheck(StepTest)}.
     *
     * @return a stream of StepTest
     */
    static Stream<StepTest> parseCheckSource() {
        return Stream.of(
                new StepTest("not card", "not entrance", "not color", "not color"),
                new StepTest("card", "not entrance", "not color", "not color"),
                new StepTest("not card", "hall", "not color", "not color"),
                new StepTest("card", "entrance", "not color", "not color"),
                new StepTest("card", "entrance", "BLUE", "not color"),
                new StepTest("card", "entrance", "not color", "RED")
        );
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        CharacterStep step = new CharacterStep();
        step.setParameter("card", "RED");
        step.setParameter("entrance", "BLUE");
        Jester withStudents = (Jester) j.add(new Student(PieceColor.RED));
        Tuple<ActionPhase, Character> after = withStudents.doEffect(ap, step);

        assertEquals(1, after.getFirst().getTable().getBoardOf(ann).getEntrance().size(PieceColor.RED));
        assertEquals(1, ((StudentStoreCharacter) after.getSecond()).size(PieceColor.BLUE));
        assertEquals(CharacterType.JESTER.getInitialCost() + 1, after.getSecond().getCost());
    }

    /**
     * Check that trying to invoke the effect passing a color not present on the card throws an exception
     */
    @Test
    void doEffect_notOnCard() {
        CharacterStep step = new CharacterStep();
        step.setParameter("card", "PINK");
        step.setParameter("entrance", "BLUE");
        Jester withStudents = (Jester) j.add(new Student(PieceColor.RED));
        assertThrows(InvalidPhaseUpdateException.class, () -> withStudents.doEffect(ap, step));
    }

    /**
     * Check that trying to invoke the effect passing a color not present in the player's entrance throws an exception
     */
    @Test
    void doEffect_notInEntrance() {
        CharacterStep step = new CharacterStep();
        step.setParameter("card", "RED");
        step.setParameter("entrance", "PINK");
        Jester withStudents = (Jester) j.add(new Student(PieceColor.RED));
        assertThrows(InvalidPhaseUpdateException.class, () -> withStudents.doEffect(ap, step));
    }

    /**
     * Check that invoking the effect with everything filled to the brim does not throw an exception
     */
    @Test
    void doEffect_full() {
        Jester withStudents = j;
        while (!withStudents.isFull())
            withStudents = (Jester) withStudents.add(new Student(PieceColor.RED));
        ActionPhase full = ap.updateTable(t -> t.updateBoardOf(ann, b -> b.updateEntrance(e -> {
            while (!e.isFull())
                e = e.add(new Student(PieceColor.BLUE));
            return e;
        })));
        CharacterStep step = new CharacterStep();
        step.setParameter("card", "RED");
        step.setParameter("entrance", "BLUE");
        Jester finalWithStudents = withStudents;
        assertAll(() -> finalWithStudents.doEffect(full, step, step, step));
    }

    /**
     * Check that if more than three steps are passed, the exceeding ones are ignored
     */
    @Test
    void doEffect_excessSteps() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Jester withStudents = j;
        for (int i = 0; i < 3; i++)
            withStudents = (Jester) withStudents.add(new Student(PieceColor.RED));
        ActionPhase full = ap.updateTable(t -> t.updateBoardOf(ann, b -> b.updateEntrance(e -> {
            for (int i = 0; i < 2; i++)
                e = e.add(new Student(PieceColor.BLUE));
            return e;
        })));
        CharacterStep step = new CharacterStep();
        step.setParameter("card", "RED");
        step.setParameter("entrance", "BLUE");
        Tuple<ActionPhase, Character> after = withStudents.doEffect(full, step, step, step, step);
        assertEquals(3, after.getFirst().getTable().getBoardOf(ann).getEntrance().size(PieceColor.RED));
        assertEquals(3, ((StudentStoreCharacter) after.getSecond()).size(PieceColor.BLUE));
    }

    /**
     * Simple data holder used by {@link #parseCheck(StepTest)} and {@link #parseCheckSource()}.
     */
    private static class StepTest {
        public String cardKey;
        public String entranceKey;
        public String cardValue;
        public String entranceValue;

        public StepTest(String cardKey, String entranceKey, String cardValue, String entranceValue) {
            this.cardKey = cardKey;
            this.entranceKey = entranceKey;
            this.cardValue = cardValue;
            this.entranceValue = entranceValue;
        }
    }
}