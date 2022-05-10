package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
     * Bound check doEffect()
     */
    @ParameterizedTest
    @MethodSource("boundCheckSource")
    void boundCheckDoEffect(StepTest t) {
        CharacterStep wrong = new CharacterStep();
        wrong.setParameter(t.entranceKey, t.entranceValue);
        wrong.setParameter(t.hallKey, t.hallValue);
        assertThrows(IllegalArgumentException.class, () -> b.doEffect(ap, null));
        assertThrows(InvalidCharacterParameterException.class, () -> b.doEffect(ap, new CharacterStep[]{wrong}));
    }

    /**
     * Generates test cases for {@link #boundCheckDoEffect(StepTest)}.
     *
     * @return a stream of StepTest
     */
    static Stream<StepTest> boundCheckSource() {
        return Stream.of(
                new StepTest("not entrance", "not hall", "not color", "not color"),
                new StepTest("entrance", "not hall", "not color", "not color"),
                new StepTest("not entrance", "hall", "not color", "not color"),
                new StepTest("entrance", "hall", "not color", "not color"),
                new StepTest("entrance", "hall", "BLUE", "not color"),
                new StepTest("entrance", "hall", "not color", "RED"),
                new StepTest("entrance", "hall", "PINK", "RED"),
                new StepTest("entrance", "hall", "BLUE", "PINK")
        );
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException {
        CharacterStep step = new CharacterStep();
        step.setParameter("entrance", "BLUE");
        step.setParameter("hall", "RED");
        Tuple<ActionPhase, Character> after = b.doEffect(ap, new CharacterStep[]{step});

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
     * Simple data holder used by {@link #boundCheckDoEffect(StepTest)} and {@link #boundCheckSource()}.
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