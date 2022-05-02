package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                        .receiveDeck(deck)
                        .playAssistant(AssistantType.CHEETAH)
                        .updateEntrance(c -> c.add(new Student(PieceColor.BLUE))));
        ap = new MockActionPhase(t, ann);
        j = new Jester();
    }

    /**
     * Bound check doEffect()
     */
    @ParameterizedTest
    @MethodSource("boundCheckSource")
    void boundCheckDoEffect(StepTest t) {
        CharacterStep wrong = new CharacterStep();
        wrong.setParameter(t.cardKey, t.cardValue);
        wrong.setParameter(t.entranceKey, t.entranceValue);
        assertThrows(IllegalArgumentException.class, () -> j.doEffect(ap, null));
        assertThrows(InvalidCharacterParameterException.class, () -> j.doEffect(ap, new CharacterStep[]{wrong}));
    }

    /**
     * Generates test cases for {@link #boundCheckDoEffect(StepTest)}.
     *
     * @return a stream of StepTest
     */
    static Stream<StepTest> boundCheckSource() {
        return Stream.of(
                new StepTest("not card", "not entrance", "not color", "not color"),
                new StepTest("card", "not entrance", "not color", "not color"),
                new StepTest("not card", "hall", "not color", "not color"),
                new StepTest("card", "entrance", "not color", "not color"),
                new StepTest("card", "entrance", "BLUE", "not color"),
                new StepTest("card", "entrance", "not color", "RED"),
                new StepTest("card", "entrance", "PINK", "RED"),
                new StepTest("card", "entrance", "BLUE", "PINK")
        );
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way
     */
    @Test
    void doEffect() throws InvalidCharacterParameterException {
        CharacterStep step = new CharacterStep();
        step.setParameter("card", "RED");
        step.setParameter("entrance", "BLUE");
        Jester withStudents = (Jester) j.add(new Student(PieceColor.RED));
        Tuple<ActionPhase, Character> after = withStudents.doEffect(ap, new CharacterStep[]{step});

        assertEquals(1, after.getFirst().getTable().getBoardOf(ann).getEntrance().size(PieceColor.RED));
        assertEquals(1, ((StudentStoreCharacter) after.getSecond()).size(PieceColor.BLUE));
        assertEquals(CharacterType.JESTER.getInitialCost() + 1, after.getSecond().getCost());
    }

    /**
     * Simple data holder used by {@link #boundCheckDoEffect(StepTest)} and {@link #boundCheckSource()}.
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