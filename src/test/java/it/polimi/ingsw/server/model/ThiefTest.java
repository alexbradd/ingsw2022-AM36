package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Thief
 */
class ThiefTest {
    private static final Player ann = new Player("ann"),
            bob = new Player("bob");
    private static Table t;
    private static Thief thief;

    /**
     * Creates common state
     */
    @BeforeAll
    static void setup() {
        t = new Table()
                .addPlayer(ann, 7, 8, TowerColor.BLACK)
                .addPlayer(bob, 7, 8, TowerColor.WHITE);
        thief = new Thief();
    }

    /**
     * Check that doEffect throws if passed null
     */
    @Test
    void nullCheck() {
        ActionPhase ap = new MockActionPhase(t, ann);
        assertThrows(IllegalArgumentException.class, () -> thief.doEffect(null));
        assertThrows(IllegalArgumentException.class, () -> thief.doEffect(ap, (CharacterStep[]) null));
        assertThrows(IllegalArgumentException.class, () -> thief.doEffect(ap, (CharacterStep) null));
    }

    /**
     * Bound check doEffect()
     */
    @Test
    void boundCheckDoEffect() {
        ActionPhase ap = new MockActionPhase(t, ann);
        CharacterStep wrong1 = new CharacterStep();
        wrong1.setParameter("not color", "not color");
        CharacterStep wrong2 = new CharacterStep();
        wrong2.setParameter("color", "not color");

        assertThrows(InvalidCharacterParameterException.class, () -> thief.doEffect(ap));
        assertThrows(InvalidCharacterParameterException.class, () -> thief.doEffect(ap, wrong1));
        assertThrows(InvalidCharacterParameterException.class, () -> thief.doEffect(ap, wrong2));
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way when everybody has at
     * least two students in the Hall
     */
    @Test
    void doEffect_atLeastTwoStudents() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Table withStudents = t
                .updateBoardOf(ann, b -> b.updateHall(h -> h
                        .add(new Student(PieceColor.RED))
                        .add(new Student(PieceColor.RED))
                        .add(new Student(PieceColor.RED))))
                .updateBoardOf(bob, b -> b.updateHall(h -> h
                        .add(new Student(PieceColor.RED))
                        .add(new Student(PieceColor.RED))));
        ActionPhase ap = new MockActionPhase(withStudents, ann);
        CharacterStep step = new CharacterStep();
        step.setParameter("color", "RED");
        Tuple<ActionPhase, Character> after = thief.doEffect(ap, step);

        assertEquals(CharacterType.THIEF.getInitialCost() + 1, after.getSecond().getCost());
        assertEquals(1, after.getFirst().getTable().getBoardOf(ann).getHall().size());
        assertEquals(0, after.getFirst().getTable().getBoardOf(bob).getHall().size());
        assertEquals(4, after.getFirst().getTable().getSack().size());
        assertEquals(Optional.of(ann), after.getFirst()
                .getTable()
                .getProfessors()
                .stream()
                .filter(p -> p.getColor() == PieceColor.RED)
                .findAny()
                .orElseThrow()
                .getOwner());
    }

    /**
     * Check that doEffect() modifies both the Character and the ActionPhase in the expected way when not everybody has
     * at least two students in the Hall
     */
    @Test
    void doEffect_lessThenTwoStudents() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Table withStudents = t
                .updateBoardOf(ann, b -> b.updateHall(h -> h
                        .add(new Student(PieceColor.RED))
                        .add(new Student(PieceColor.RED))
                        .add(new Student(PieceColor.RED))))
                .updateBoardOf(bob, b -> b.updateHall(h -> h
                        .add(new Student(PieceColor.RED))));
        ActionPhase ap = new MockActionPhase(withStudents, ann);
        CharacterStep step = new CharacterStep();
        step.setParameter("color", "RED");
        Tuple<ActionPhase, Character> after = thief.doEffect(ap, step);

        assertEquals(CharacterType.THIEF.getInitialCost() + 1, after.getSecond().getCost());
        assertEquals(1, after.getFirst().getTable().getBoardOf(ann).getHall().size());
        assertEquals(0, after.getFirst().getTable().getBoardOf(bob).getHall().size());
        assertEquals(3, after.getFirst().getTable().getSack().size());
        assertEquals(Optional.of(ann), after.getFirst()
                .getTable()
                .getProfessors()
                .stream()
                .filter(p -> p.getColor() == PieceColor.RED)
                .findAny()
                .orElseThrow()
                .getOwner());
    }

    /**
     * Check that doEffect ignores any exceeding steps passed
     */
    @Test
    void doEffect_exceedingSteps() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Table withStudents = t
                .updateBoardOf(ann, b -> b.updateHall(h -> h
                        .add(new Student(PieceColor.RED))
                        .add(new Student(PieceColor.RED))
                        .add(new Student(PieceColor.RED))))
                .updateBoardOf(bob, b -> b.updateHall(h -> h
                        .add(new Student(PieceColor.RED))
                        .add(new Student(PieceColor.RED))));
        ActionPhase ap = new MockActionPhase(withStudents, ann);
        CharacterStep step1 = new CharacterStep();
        step1.setParameter("color", "RED");
        CharacterStep step2 = new CharacterStep();
        step2.setParameter("color", "PINK");
        Tuple<ActionPhase, Character> after = thief.doEffect(ap, step1, step2);

        assertEquals(1, after.getFirst().getTable().getBoardOf(ann).getHall().size());
        assertEquals(0, after.getFirst().getTable().getBoardOf(bob).getHall().size());
        assertEquals(4, after.getFirst().getTable().getSack().size());
        assertEquals(Optional.of(ann), after.getFirst()
                .getTable()
                .getProfessors()
                .stream()
                .filter(p -> p.getColor() == PieceColor.RED)
                .findAny()
                .orElseThrow()
                .getOwner());
    }
}