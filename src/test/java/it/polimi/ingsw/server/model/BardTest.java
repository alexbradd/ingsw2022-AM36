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
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for the Bard character
 */
class BardTest {
    private static Player ann;
    private static ActionPhase ap;
    private static Bard b;

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
                        .updateEntrance(c -> c.add(new Student(PieceColor.BLUE)))
                        .updateHall(c -> c.add(new Student(PieceColor.RED))));
        ap = new MockActionPhase(t, ann);
        b = new Bard();
    }

    /**
     * Null check doEffect
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> b.doEffect(null));
        assertThrows(IllegalArgumentException.class, () -> b.doEffect(ap, (CharacterStep[]) null));
        assertThrows(IllegalArgumentException.class, () -> b.doEffect(ap, (CharacterStep) null));
    }

    /**
     * Test that doEffect() parses steps correctly
     */
    @ParameterizedTest
    @MethodSource("parseCheckSource")
    void parseCheck(StepTest t) {
        CharacterStep wrong = new CharacterStep();
        wrong.setParameter(t.entranceKey, t.entranceValue);
        wrong.setParameter(t.hallKey, t.hallValue);
        assertThrows(InvalidCharacterParameterException.class, () -> b.doEffect(ap, wrong));
    }

    /**
     * Generates test cases for {@link #parseCheck(StepTest)}.
     *
     * @return a stream of StepTest
     */
    static Stream<StepTest> parseCheckSource() {
        return Stream.of(
                new StepTest("not entrance", "not hall", "not color", "not color"),
                new StepTest("entrance", "not hall", "not color", "not color"),
                new StepTest("not entrance", "hall", "not color", "not color"),
                new StepTest("entrance", "hall", "not color", "not color"),
                new StepTest("entrance", "hall", "BLUE", "not color"),
                new StepTest("entrance", "hall", "not color", "RED")
        );
    }

    /**
     * Checks that if we invoke the effect passing a color no present in the entrance or hall, an exception is thrown.
     */
    @Test
    void withEmptyColor() {
        CharacterStep step = new CharacterStep();
        step.setParameter("entrance", "PINK");
        step.setParameter("hall", "RED");
        assertThrows(InvalidPhaseUpdateException.class, () -> b.doEffect(ap, step));

        step.setParameter("entrance", "BLUE");
        step.setParameter("hall", "PINK");
        assertThrows(InvalidPhaseUpdateException.class, () -> b.doEffect(ap, step));
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        CharacterStep step = new CharacterStep();
        step.setParameter("entrance", "BLUE");
        step.setParameter("hall", "RED");
        Tuple<ActionPhase, Character> after = b.doEffect(ap, step);

        assertEquals(1, after.getFirst().getTable().getBoardOf(ann).getEntrance().size(PieceColor.RED));
        assertEquals(1, after.getFirst().getTable().getBoardOf(ann).getHall().size(PieceColor.BLUE));
        assertEquals(CharacterType.BARD.getInitialCost() + 1, after.getSecond().getCost());
        assertEquals(Optional.of(ann), after.getFirst()
                .getTable()
                .getProfessors()
                .stream()
                .filter(p -> p.getColor() == PieceColor.BLUE)
                .findAny()
                .orElseThrow()
                .getOwner());
    }

    /**
     * Test that if more then two steps are given, the exceeding one are ignored
     */
    @Test
    void withTooManySteps() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        ActionPhase phase = ap.updateTable(t -> t.updateBoardOf(ann, b -> b
                .updateEntrance(e -> e
                        .add(new Student(PieceColor.BLUE))
                        .add(new Student(PieceColor.BLUE)))
                .updateHall(h -> h
                        .add(new Student(PieceColor.RED))
                        .add(new Student(PieceColor.RED)))));
        CharacterStep step = new CharacterStep();
        step.setParameter("entrance", "BLUE");
        step.setParameter("hall", "RED");
        Tuple<ActionPhase, Character> after = b.doEffect(phase, step, step, step);
        assertEquals(2, after.getFirst().getTable().getBoardOf(ann).getEntrance().size(PieceColor.RED));
        assertEquals(2, after.getFirst().getTable().getBoardOf(ann).getHall().size(PieceColor.BLUE));
    }

    /**
     * Test that everything goes smoothly with everything full to the brim
     */
    @Test
    void withEverythingFull() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        ActionPhase full = ap.updateTable(t -> t.updateBoardOf(ann, b -> b
                .updateEntrance(e -> {
                    while (!e.isFull())
                        e = e.add(new Student(PieceColor.BLUE));
                    return e;
                })
                .updateHall(h -> {
                    for (PieceColor c : PieceColor.values())
                        while (!h.isFull(c))
                            h = h.add(new Student(c));
                    return h;
                })
        ));
        CharacterStep step = new CharacterStep();
        step.setParameter("entrance", "BLUE");
        step.setParameter("hall", "BLUE");
        Tuple<ActionPhase, Character> after = b.doEffect(full, step);

        assertEquals(7, after.getFirst().getTable().getBoardOf(ann).getEntrance().size(PieceColor.BLUE));
        assertEquals(10, after.getFirst().getTable().getBoardOf(ann).getHall().size(PieceColor.BLUE));
    }

    /**
     * Simple data holder used by {@link #parseCheck(StepTest)} and {@link #parseCheckSource()}.
     */
    private static class StepTest {
        public String entranceKey;
        public String hallKey;
        public String entranceValue;
        public String hallValue;

        public StepTest(String entranceKey, String hallKey, String entranceValue, String hallValue) {
            this.entranceKey = entranceKey;
            this.hallKey = hallKey;
            this.entranceValue = entranceValue;
            this.hallValue = hallValue;
        }
    }
}