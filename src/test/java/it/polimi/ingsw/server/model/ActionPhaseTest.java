package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ActionPhase's methods. Since the class is abstract, a mock will be used.
 */
public class ActionPhaseTest {
    private static final PieceColor annColor = PieceColor.RED;
    private static final PieceColor bobColor = PieceColor.BLUE;
    private static Player ann, bob;
    private static Table t;
    private static CharacterStep[] herbalistSteps;

    /**
     * Sets up common starting state.
     */
    @BeforeAll
    static void setup() {
        ann = new Player("ann");
        bob = new Player("bob");
        herbalistSteps = new CharacterStep[]{new CharacterStep()};
        List<Assistant> deckAnn = new ArrayList<>(),
                deckBob = new ArrayList<>();
        deckAnn.add(new Assistant(AssistantType.CHEETAH, Mage.MAGE));
        deckBob.add(new Assistant(AssistantType.OSTRICH, Mage.FAIRY));
        t = new Table()
                .addPlayer(ann, 7, 8, TowerColor.BLACK)
                .addPlayer(bob, 7, 8, TowerColor.WHITE)
                .updateProfessors(ps -> ps.stream()
                        .map(p -> {
                            if (p.getColor().equals(annColor)) return new Professor(annColor, ann);
                            if (p.getColor().equals(bobColor)) return new Professor(bobColor, bob);
                            return p;
                        })
                        .toList())
                .updateBoardOf(ann, b -> {
                    b = b.receiveDeck(Mage.MAGE, deckAnn).playAssistant(AssistantType.CHEETAH);
                    for (int i = 0; i < 8; i++)
                        b = b.receiveTower(new Tower(TowerColor.BLACK, ann));
                    return b;
                })
                .updateBoardOf(bob, b -> {
                    b = b.receiveDeck(Mage.FAIRY, deckBob).playAssistant(AssistantType.OSTRICH);
                    for (int i = 0; i < 8; i++)
                        b = b.receiveTower(new Tower(TowerColor.WHITE, bob));
                    return b;
                })
                .updateBoardOf(ann, b -> b.updateEntrance(c -> {
                    for (int i = 0; i < 7; i++)
                        c = c.add(new Student(annColor));
                    return c;
                }))
                .updateBoardOf(bob, b -> b.updateEntrance(c -> {
                    for (int i = 0; i < 7; i++)
                        c = c.add(new Student(bobColor));
                    return c;
                }))
                .updateCharacters(cs -> {
                    cs.add(new MockHerbalist());
                    return cs;
                });
    }

    /**
     * Null check methods
     */
    @Test
    void nullCheck() {
        ActionPhase phase = new MockActionPhase(t, ann);
        assertThrows(IllegalArgumentException.class, () -> phase.assignTower(null));
        assertThrows(IllegalArgumentException.class, () -> phase.blockIsland(0, null));
        assertThrows(IllegalArgumentException.class, () -> phase.playCharacter(null, null, (CharacterStep[]) null));
        assertThrows(IllegalArgumentException.class, () -> phase.playCharacter(ann, null, (CharacterStep[]) null));
        assertThrows(IllegalArgumentException.class, () -> phase.playCharacter(ann, CharacterType.HERBALIST, (CharacterStep[]) null));
        assertThrows(IllegalArgumentException.class, () -> phase.setInfluenceCalculator(null));
        assertThrows(IllegalArgumentException.class, () -> phase.setMaxExtractor(null));
        assertThrows(IllegalArgumentException.class, () -> phase.getFromEntrance(null, null));
        assertThrows(IllegalArgumentException.class, () -> phase.getFromEntrance(ann, null));
        assertThrows(IllegalArgumentException.class, () -> phase.addToHall(null, null));
        assertThrows(IllegalArgumentException.class, () -> phase.addToHall(ann, null));
        assertThrows(IllegalArgumentException.class, () -> phase.getFromHall(null, null));
        assertThrows(IllegalArgumentException.class, () -> phase.getFromHall(ann, null));
        assertThrows(IllegalArgumentException.class, () -> phase.addToIsland(ann, 0, null));
        assertThrows(IllegalArgumentException.class, () -> phase.addToIsland(null, 0, null));
        assertThrows(IllegalArgumentException.class, () -> phase.updateTable(null));
        assertThrows(IllegalArgumentException.class, () -> phase.authorizePlayer(null));
        assertThrows(IllegalArgumentException.class, () -> phase.forEachPlayer(null));

    }

    /**
     * Checks if the phase authorizes the correct player.
     */
    @Test
    void checkAuthorizePlayer() {
        MockActionPhase phase = new MockActionPhase(t, ann);

        assertThrows(InvalidPlayerException.class, () -> phase.authorizePlayer("bob"));
        assertAll(() -> assertEquals(phase.authorizePlayer("ann"), ann));
    }

    /**
     * Test that, if the extraction is legitimate, {@link ActionPhase#getFromEntrance(Player, PieceColor)} extracts and
     * returns a student
     */
    @Test
    void testGetFromEntrance() throws InvalidPhaseUpdateException {
        ActionPhase phase = new MockActionPhase(t, ann);

        for (int i = 0; i < 7; i++) {
            phase = phase.getFromEntrance(ann, annColor).getFirst();
            assertEquals(7 - i - 1, phase.getTable().getBoardOf(ann).getEntrance().size());
        }
        ActionPhase finalPhase = phase;
        assertThrows(InvalidPhaseUpdateException.class, () -> finalPhase.getFromEntrance(ann, annColor));
    }

    /**
     * Test that an exception is thrown if too many calls to {@link ActionPhase#addToHall(Player, Student)} are made.
     */
    @Test
    void testAddToHall() throws InvalidPhaseUpdateException {
        ActionPhase phase = new MockActionPhase(t, ann);

        for (int i = 0; i < 10; i++)
            phase = phase.addToHall(ann, new Student(annColor));
        ActionPhase finalPhase = phase;
        assertThrows(InvalidPhaseUpdateException.class, () -> finalPhase.addToHall(ann, new Student(annColor)));
    }

    /**
     * Tests if the phase updates students in the hall and professors after
     * {@link ActionPhase#addToHall(Player, Student)}
     */
    @Test
    void testUpdateHall() throws InvalidPhaseUpdateException {
        ActionPhase phase = new MockActionPhase(t, ann);
        Phase afterUpdate = phase.addToHall(ann, new Student(annColor));
        afterUpdate.getTable().getBoardOf(ann).updateHall(c -> {
            assertEquals(1, c.size());
            return c;
        });
        afterUpdate.getTable().getBoardOf(bob).updateHall(c -> {
            assertEquals(0, c.size());
            return c;
        });
        Optional<Player> profOwner = afterUpdate.getTable().getProfessors().stream()
                .filter(p -> p.getColor().equals(annColor))
                .findAny()
                .orElseThrow()
                .getOwner();
        assertTrue(profOwner.isPresent());
        assertEquals(ann, profOwner.get());
    }

    /**
     * Check that on 3,6,9 students a coin is given to a player.
     * <p>
     * Note: a coin is not given if the size was already 3,6,9.
     */
    @Test
    void testUpdateHallCoins() throws InvalidPhaseUpdateException {
        ActionPhase phase = new MockActionPhase(t, ann);

        for (int i = 0; i < 3; i++)
            phase = phase.addToHall(ann, new Student(annColor));
        assertEquals(1, phase.getTable().getBoardOf(ann).getCoins());

        for (int i = 0; i < 3; i++)
            phase = phase.addToHall(ann, new Student(annColor));
        assertEquals(2, phase.getTable().getBoardOf(ann).getCoins());

        for (int i = 0; i < 3; i++)
            phase = phase.addToHall(ann, new Student(annColor));
        assertEquals(3, phase.getTable().getBoardOf(ann).getCoins());
    }

    /**
     * Test that {@link ActionPhase#addToHall(Player, Student)} respects its contract
     */
    @Test
    void testAddToIsland() throws InvalidPhaseUpdateException {
        ActionPhase phase = new MockActionPhase(t, ann);

        assertThrows(InvalidPhaseUpdateException.class, () -> phase.addToIsland(ann, -1, new Student(annColor)));
        assertThrows(InvalidPhaseUpdateException.class, () -> phase.addToIsland(ann, 15, new Student(annColor)));

        Phase afterUpdate = phase.addToIsland(ann, 0, new Student(annColor));
        Island updated = afterUpdate.getTable().getIslandList().get(0);
        assertEquals(1, updated.getStudents().size());
    }

    /**
     * Test that {@link ActionPhase#addToHall(Player, Student)} correctly adds students to the correct index even after
     * a merge
     */
    @Test
    void testAddToIslandAfterMerge() throws InvalidPhaseUpdateException {
        Table table = t.updateIslandList(islands -> {
            Island merge1 = islands.remove(0).updateTowers(ts -> {
                ts.add(new Tower(TowerColor.WHITE, ann));
                return ts;
            });
            Island merge2 = islands.remove(0).updateTowers(ts -> {
                ts.add(new Tower(TowerColor.WHITE, ann));
                return ts;
            });
            Island merge = merge1.merge(merge2);
            islands.add(0, merge);
            return islands;
        });
        ActionPhase phase = new MockActionPhase(table, ann);
        Phase afterUpdate = phase.addToIsland(ann, 11, new Student(annColor));
        Island updated = afterUpdate.getTable().getIslandList().get(10);
        assertEquals(1, updated.getStudents().size());
    }

    /**
     * Test that update* functions abort when null is returned
     */
    @Test
    void testNullUpdate() {
        ActionPhase phase = new MockActionPhase(t, ann);

        Phase u = phase.updateTable(t -> null);
        assertEquals(u.getTable(), phase.getTable());
    }

    /**
     * Tests that {@link ActionPhase#forEachPlayer(Function)} respects its contract
     */
    @Test
    void testForEachPlayer() {
        ActionPhase phase = new MockActionPhase(t, ann);
        phase = phase.forEachPlayer(b -> {
            if (Objects.equals(b.getPlayer(), ann))
                return b.updateEntrance(e -> e.remove().getFirst());
            return null;
        });
        assertFalse(phase.getTable().getBoards().stream().anyMatch(Objects::isNull));
        assertEquals(6, phase.getTable().getBoardOf(ann).getEntrance().size());
        assertEquals(7, phase.getTable().getBoardOf(bob).getEntrance().size());
    }

    /**
     * Tests that draw students returns students from the sack, if there are any
     */
    @Test
    void testDrawStudent() {
        Student s = new Student(annColor);
        Table withStudent = t.updateSack(sack -> sack.add(s));
        ActionPhase phase = new MockActionPhase(t, ann);

        assertTrue(phase.drawStudent().getSecond().isEmpty());

        phase = new MockActionPhase(withStudent, ann);
        Tuple<ActionPhase, Optional<Student>> draw = phase.drawStudent();
        assertTrue(draw.getSecond().isPresent());
        assertEquals(draw.getSecond().get(), s);
    }

    /**
     * Test that the island requested to be blocked is indeed blocked
     */
    @Test
    void testBlockIsland() {
        BlockCard b = new BlockCard(CharacterType.HERBALIST);
        ActionPhase phase = new MockActionPhase(t, ann);

        assertFalse(phase.getTable().getIslandList().get(0).isBlocked());
        phase = phase.blockIsland(0, b);
        assertTrue(phase.getTable().getIslandList().get(0).isBlocked());
        assertEquals(b, phase.getTable().getIslandList().get(0).popBlock().getSecond());
    }

    /**
     * Test that {@link ActionPhase#blockIsland(int, BlockCard)} correctly block the island with the correct index
     */
    @Test
    void testBlockAfterMerge() {
        Table table = t.updateIslandList(islands -> {
            Island merge1 = islands.remove(0).updateTowers(ts -> {
                ts.add(new Tower(TowerColor.WHITE, ann));
                return ts;
            });
            Island merge2 = islands.remove(0).updateTowers(ts -> {
                ts.add(new Tower(TowerColor.WHITE, ann));
                return ts;
            });
            Island merge = merge1.merge(merge2);
            islands.add(0, merge);
            return islands;
        });
        ActionPhase phase = new MockActionPhase(table, ann);
        Phase afterUpdate = phase.blockIsland(11, new BlockCard(CharacterType.HERBALIST));
        Island updated = afterUpdate.getTable().getIslandList().get(10);
        assertEquals(1, updated.getNumOfBlocks());
    }

    /**
     * Test that after playCharacter a character is marked as played
     */
    @Test
    void testPlayCharacter() throws InvalidPhaseUpdateException, InvalidCharacterParameterException {
        Table withCharacter = t.updateBoardOf(ann, b -> b.receiveCoin().receiveCoin());
        ActionPhase phase = new MockActionPhase(withCharacter, ann);

        assertFalse(phase.hasPlayedCharacter());
        phase = (ActionPhase) phase.playCharacter(ann, CharacterType.HERBALIST, herbalistSteps);
        assertTrue(phase.hasPlayedCharacter());
    }

    /**
     * Test that character are not played if player doesn't have enough coins
     */
    @Test
    void testPlayCharacterNotEnoughCoins() {
        ActionPhase phase = new MockActionPhase(t, ann);

        assertFalse(phase.hasPlayedCharacter());
        assertThrows(InvalidPhaseUpdateException.class, () ->
                phase.playCharacter(ann, CharacterType.HERBALIST, herbalistSteps));
    }

    /**
     * Test that characterPlayed flag is persisted through actionPhase changes
     */
    @Test
    void testPlayCharacterPersistFlag() throws InvalidPhaseUpdateException, InvalidCharacterParameterException {
        Table withCharacter = t.updateBoardOf(ann, b -> b.receiveCoin().receiveCoin());
        MockActionPhase phase = (MockActionPhase) new MockActionPhase(withCharacter, ann)
                .playCharacter(ann, CharacterType.HERBALIST, herbalistSteps);

        assertTrue(phase.hasPlayedCharacter());
        phase = (MockActionPhase) phase.mockPhaseChangeOperation();
        assertTrue(phase.hasPlayedCharacter());
    }

    /**
     * Mocks a Herbalist card
     */
    private static class MockHerbalist extends Character {

        /**
         * Base constructor. Sets up only the card's initial cost and character
         */
        MockHerbalist() {
            super(CharacterType.HERBALIST);
        }

        MockHerbalist(MockHerbalist old) {
            super(old);
        }

        /**
         * Abstract method that returns a shallow copy of the current object.
         *
         * @return returns a shallow copy of the current object.
         */
        @Override
        Character shallowCopy() {
            return new MockHerbalist(this);
        }
    }
}
