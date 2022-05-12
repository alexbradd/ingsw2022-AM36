package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.server.model.exceptions.NoTowersException;
import it.polimi.ingsw.server.model.iterators.AssistantValueIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for CloudPickPhase
 */
class CloudPickPhaseTest {
    private static final PieceColor annColor = PieceColor.RED;
    private static final PieceColor bobColor = PieceColor.BLUE;
    private static Player ann, bob;
    private static Table t;
    private static AssistantValueIterator avi;
    private static MockActionPhase previous;

    /**
     * Sets up common starting state.
     */
    @BeforeAll
    static void setup() {
        ann = new Player("ann");
        bob = new Player("bob");
        List<Assistant> deckAnn = new ArrayList<>(),
                deckBob = new ArrayList<>();
        deckAnn.add(new Assistant(AssistantType.CHEETAH, Mage.MAGE));
        deckAnn.add(new Assistant(AssistantType.CAT, Mage.MAGE));
        deckBob.add(new Assistant(AssistantType.OSTRICH, Mage.FAIRY));
        deckBob.add(new Assistant(AssistantType.EAGLE, Mage.FAIRY));
        t = new Table()
                .addPlayer(ann, 7, 8, TowerColor.BLACK)
                .addPlayer(bob, 7, 8, TowerColor.WHITE)
                .updateBoardOf(ann, b -> b
                        .receiveDeck(Mage.MAGE, deckAnn)
                        .playAssistant(AssistantType.CHEETAH)
                        .receiveTower(new Tower(TowerColor.BLACK, ann)))
                .updateBoardOf(bob, b -> b
                        .receiveDeck(Mage.FAIRY, deckBob)
                        .playAssistant(AssistantType.OSTRICH)
                        .receiveTower(new Tower(TowerColor.WHITE, bob)))
                .updateClouds(cs -> {
                    HashSet<Student> s = new HashSet<>();
                    s.add(new Student(annColor));
                    s.add(new Student(bobColor));
                    s.add(new Student(annColor));
                    cs.add(new Cloud(3).refillCloud(new HashSet<>(s)));
                    cs.add(new Cloud(3).refillCloud(new HashSet<>(s)));
                    return cs;
                });
        previous = new MockActionPhase(t, ann);
        avi = new AssistantValueIterator(t.getBoards(), 0);
        avi.next();
    }

    /**
     * Checks that null is rejected.
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> new CloudPickPhase(null, null));
        assertThrows(IllegalArgumentException.class, () -> new CloudPickPhase(previous, null));
        assertThrows(IllegalArgumentException.class, () -> new CloudPickPhase(previous, avi).drainCloud(null, 0));
    }

    /**
     * Bound check drainCloud()
     */
    @Test
    void boundCheckDrainCloud() {
        CloudPickPhase phase = new CloudPickPhase(previous, avi);
        assertThrows(InvalidPhaseUpdateException.class, () -> phase.drainCloud(ann, -1));
        assertThrows(InvalidPhaseUpdateException.class, () -> phase.drainCloud(ann, 6));
    }

    /**
     * Check that forbidden operations throw
     */
    @Test
    void checkOps() {
        CloudPickPhase p = new CloudPickPhase(previous, avi);
        assertThrows(UnsupportedOperationException.class, () -> p.chooseMageDeck(ann, Mage.MAGE));
        assertThrows(UnsupportedOperationException.class, () -> p.moveMn(ann, 0));
        assertThrows(UnsupportedOperationException.class, () -> p.markStudentMove(ann));
        assertThrows(UnsupportedOperationException.class, () -> p.addPlayer("carl"));
        assertThrows(UnsupportedOperationException.class, () -> p.removePlayer("ann"));
        assertThrows(UnsupportedOperationException.class, () -> p.playAssistant(ann, AssistantType.CHEETAH));
    }

    /**
     * Checks if the phase authorizes the correct player.
     */
    @Test
    void checkAuthorizePlayer() {
        CloudPickPhase phase = new CloudPickPhase(previous, avi);

        assertThrows(InvalidPlayerException.class, () -> phase.authorizePlayer("bob"));
        assertAll(() -> assertEquals(phase.authorizePlayer("ann"), ann));
    }

    /**
     * Checks that if the current player is not the last player drainCloud() loops back to StudentMovePhase for the
     * next player.
     */
    @Test
    void notLastPlayer() throws InvalidPhaseUpdateException {
        Table withSack = t.updateSack(s -> s.add(new Student(annColor)));
        ActionPhase phase = new CloudPickPhase(new MockActionPhase(withSack, ann), avi);

        phase = (ActionPhase) phase.drainCloud(ann, 0);

        assertInstanceOf(StudentMovePhase.class, phase);
        assertEquals(bob, phase.getCurrentPlayer());
        assertEquals(3, phase.getTable().getBoardOf(ann).getEntrance().size());
    }

    /**
     * Checks that if the current player is the last player drainCloud() goes to Planning phase
     */
    @Test
    void withNoPlayersLeft() throws InvalidPhaseUpdateException {
        Table withSack = t.updateSack(s -> s.add(new Student(annColor)));
        AssistantValueIterator bobAvi = new AssistantValueIterator(avi);
        bobAvi.next();
        assertFalse(bobAvi.hasNext());

        Phase phase = new CloudPickPhase(new MockActionPhase(withSack, bob), bobAvi);
        phase = phase.drainCloud(bob, 0);

        assertInstanceOf(PlanningPhase.class, phase);
        assertEquals(3, phase.getTable().getBoardOf(bob).getEntrance().size());
    }

    /**
     * Checks that the player cannot drain an empty cloud
     */
    @Test
    void noEmptyCloud() {
        Table withEmptyCloud = t.updateClouds(cs -> {
            Cloud c = cs.remove(0);
            c = c.drainCloud().getFirst();
            cs.add(0, c);
            return cs;
        });
        Phase phase = new CloudPickPhase(new MockActionPhase(withEmptyCloud, ann), avi);
        assertThrows(InvalidPhaseUpdateException.class, () -> phase.drainCloud(ann, 0));
    }

    /**
     * Checks that pickCloud sends us to EndGamePhase if:
     * 1. we are the last player in the round
     * 2. there are no students left in the sack
     */
    @Test
    void endGameIfNoStudents() throws InvalidPhaseUpdateException {
        AssistantValueIterator customAvi = new AssistantValueIterator(avi);
        Phase phase = new CloudPickPhase(new MockActionPhase(t, ann), customAvi);

        phase = phase.drainCloud(ann, 0);

        assertInstanceOf(StudentMovePhase.class, phase);

        customAvi.next();
        phase = new CloudPickPhase(new MockActionPhase(t, bob), customAvi);

        phase = phase.drainCloud(bob, 1);
        assertInstanceOf(EndgamePhase.class, phase);
    }

    /**
     * Checks that pickCloud sends us to EndGamePhase if:
     * 1. we are the last player in the round
     * 2. somebody played their last assistant
     */
    @Test
    void endGameIfNoAssistants() throws InvalidPhaseUpdateException {
        Table withNoAssistants = t.updateSack(s -> s.add(new Student(annColor)))
                .updateBoardOf(ann, b -> b.playAssistant(AssistantType.CAT));
        AssistantValueIterator customAvi = new AssistantValueIterator(avi);
        Phase phase = new CloudPickPhase(new MockActionPhase(withNoAssistants, ann), customAvi);

        phase = phase.drainCloud(ann, 0);

        assertInstanceOf(StudentMovePhase.class, phase);

        customAvi.next();
        phase = new CloudPickPhase(new MockActionPhase(withNoAssistants, bob), customAvi);

        phase = phase.drainCloud(bob, 1);
        assertInstanceOf(EndgamePhase.class, phase);
    }

    /**
     * Test that if a player somehow finishes their towers
     */
    @Test
    void immediateEndgameTowers() throws InvalidPhaseUpdateException {
        Table withoutTowers = t.updateBoardOf(ann, board -> {
            while (true) {
                try {
                    board = board.sendTower().getFirst();
                } catch (NoTowersException ignored) {
                    break;
                }
            }
            return board;
        });
        Phase moves = new CloudPickPhase(new MockActionPhase(withoutTowers, ann), avi);
        moves = moves.drainCloud(ann, 1);
        assertInstanceOf(EndgamePhase.class, moves);
    }

    /**
     * Checks that we are immediately sent to EndGamePhase if somehow only 3 islands remain in the game
     */
    @Test
    void immediateEndgameIslands() throws InvalidPhaseUpdateException {
        Table withoutIslands = t.updateIslandList(is -> List.of(new Island(1), new Island(2), new Island(3)));
        Phase moves = new CloudPickPhase(new MockActionPhase(withoutIslands, ann), avi);
        moves = moves.drainCloud(ann, 1);
        assertInstanceOf(EndgamePhase.class, moves);
    }

    /**
     * Check that common attributes like hasPlayedCharacter, influenceCalculators or mxExtractors are reset when going
     * to another player
     */
    @Test
    void sharedStateDestruction() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Table withCharacter = t.updateCharacters(cs -> {
                    cs.add(new MockCharacter());
                    return cs;
                })
                .updateBoardOf(ann, b -> b.receiveCoin().receiveCoin())
                .updateSack(s -> s.add(new Student(annColor)));
        MockActionPhase mockActionPhase = new MockActionPhase(withCharacter, ann);
        ActionPhase phase = new CloudPickPhase(mockActionPhase, avi);
        phase = (ActionPhase) phase.playCharacter(ann, CharacterType.HERBALIST);
        phase = phase.setInfluenceCalculator(new ExtraPointsInfluenceDecorator(phase.getInfluenceCalculator(), ann, 2));
        phase = phase.setMaxExtractor(new EqualityInclusiveMaxExtractor(ann));

        phase = (ActionPhase) phase.drainCloud(ann, 0);

        assertInstanceOf(StudentMovePhase.class, phase);
        assertInstanceOf(StandardInfluenceCalculator.class, phase.getInfluenceCalculator());
        assertInstanceOf(EqualityExclusiveMaxExtractor.class, phase.getMaxExtractor());
        assertFalse(phase.hasPlayedCharacter());
        assertEquals(bob, phase.getCurrentPlayer());
    }

}