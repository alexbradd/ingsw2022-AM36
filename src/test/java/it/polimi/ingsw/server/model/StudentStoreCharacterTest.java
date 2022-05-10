package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.ThrowingBiFunction;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.exceptions.ContainerIsFullException;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EmptyStackException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for StudentStoreCharacter. Since StudentStoreCharacter is abstract, a mock concrete class will be used
 */
class StudentStoreCharacterTest {
    private static final Player ann = new Player("ann");
    private static PreparePhase pp;
    private static ActionPhase ap;
    private static MockCharacter c;

    /**
     * Creates common state
     */
    @BeforeAll
    static void setup() {
        ArrayList<Assistant> deck = new ArrayList<>();
        deck.add(new Assistant(AssistantType.CHEETAH, Mage.MAGE));
        Table t = new Table()
                .addPlayer(ann, 7, 8, TowerColor.BLACK)
                .updateBoardOf(ann, b -> b.receiveDeck(Mage.MAGE, deck).playAssistant(AssistantType.CHEETAH));
        ap = new MockActionPhase(t, ann);
        c = new MockCharacter(CharacterType.PRIEST, 10);
        pp = new MockPreparePhase();
    }

    /**
     * Bound checks
     */
    @Test
    void boundCheck() {
        assertThrows(IllegalArgumentException.class, () -> new MockCharacter(null, 0));
        assertThrows(IllegalArgumentException.class, () -> new MockCharacter(CharacterType.PRIEST, -10));
        assertThrows(IllegalArgumentException.class, () -> new MockCharacter(CharacterType.PRIEST, 0));
        assertThrows(IllegalArgumentException.class, () -> c.remove(null));
        assertThrows(IllegalArgumentException.class, () -> c.moveFromHere(null, null, null, null));
        assertThrows(IllegalArgumentException.class, () -> c.moveFromHere(new Tuple<>(ap, c), null, null, null));
        assertThrows(IllegalArgumentException.class, () -> c.moveFromHere(new Tuple<>(ap, c), PieceColor.RED, null, null));
        assertThrows(IllegalArgumentException.class, () -> c.moveFromHere(new Tuple<>(ap, c), PieceColor.RED, (a, b) -> null, null));
    }

    /**
     * Check doPrepare hook
     */
    @Test
    void doPrepare() {
        Tuple<PreparePhase, Character> m = c.doPrepare(pp);
        m.map((prepare, character) -> {
            StudentStoreCharacter ssc = (StudentStoreCharacter) character;
            assertNotEquals(0, ssc.size());
            assertEquals(ssc.getStudents().size(), 10);
            return null;
        });
        assertNotSame(pp, m.getFirst());
        assertNotSame(c, m.getSecond());
    }

    /**
     * Test remove(PieceColor)
     */
    @Test
    void removeColor() {
        StudentStoreCharacter m = (StudentStoreCharacter) c.doPrepare(pp).getSecond();
        assertAll(() -> {
            StudentStoreCharacter n = m;
            for (Student s : n.getStudents())
                n = n.remove(s.getColor()).getFirst();
            StudentStoreCharacter finalN = n;
            assertThrows(EmptyContainerException.class, () -> finalN.remove(PieceColor.RED));
        });
        assertAll(() -> {
            StudentStoreCharacter n = m;
            for (Student s : n.getStudents().stream().filter(s -> s.getColor().equals(PieceColor.RED)).toList())
                n = n.remove(s.getColor()).getFirst();
            StudentStoreCharacter finalN = n;
            assertThrows(EmptyStackException.class, () -> finalN.remove(PieceColor.RED));
        });
    }

    /**
     * Test add()
     */
    @Test
    void add() {
        assertAll(() -> {
            StudentStoreCharacter n = c;
            for (int i = 0; i < 10; i++)
                n = n.add(new Student(PieceColor.RED));
            StudentStoreCharacter finalN = n;
            assertThrows(ContainerIsFullException.class, () -> finalN.add(new Student(PieceColor.RED)));
        });
    }

    /**
     * Test that {@link StudentStoreCharacter#moveFromHere(Tuple, PieceColor, ThrowingBiFunction, ThrowingBiFunction)}
     * throws exception if there are no students on the card.
     */
    @Test
    void moveFromHere_noStudents() {
        assertThrows(InvalidPhaseUpdateException.class, () -> c.moveFromHere(new Tuple<>(ap, c), PieceColor.BLUE,
                (action, student) -> action,
                Tuple::new));
    }

    /**
     * Test that {@link StudentStoreCharacter#moveFromHere(Tuple, PieceColor, ThrowingBiFunction, ThrowingBiFunction)}
     * respects its contract.
     */
    @Test
    void moveFromHere() throws InvalidPhaseUpdateException {
        StudentStoreCharacter m = (StudentStoreCharacter) c.doPrepare(pp).getSecond();
        PieceColor toMove = m.getStudents().stream().findAny().orElseThrow().getColor();
        Tuple<ActionPhase, Character> afterMove = m.moveFromHere(new Tuple<>(ap, m), toMove,
                (action, student) -> action.addToHall(ann, student),
                Tuple::new);
        assertEquals(1, afterMove.getFirst().getTable().getBoardOf(ann).getHall().size(toMove));
        assertEquals(9, ((StudentStoreCharacter) afterMove.getSecond()).getStudents().size());
    }

    /**
     * Mock class
     */
    private static class MockCharacter extends StudentStoreCharacter {
        /**
         * Base constructor. Sets up the card's initial cost, character and maximum number of students that this card can
         * have.
         *
         * @param characterType    this card's character
         * @param maxStudentAmount the maximum amount of students this card can hold
         * @throws IllegalArgumentException if {@code characterType} is null
         */
        MockCharacter(CharacterType characterType, int maxStudentAmount) {
            super(characterType, maxStudentAmount);
        }

        /**
         * Returns a copy of the passed Character
         *
         * @param old Character to copy
         * @throws IllegalArgumentException if {@code old} is null
         */
        MockCharacter(StudentStoreCharacter old) {
            super(old);
        }

        /**
         * Abstract method that returns a shallow copy of the current object.
         *
         * @return returns a shallow copy of the current object.
         */
        @Override
        Character shallowCopy() {
            return new MockCharacter(this);
        }
    }
}