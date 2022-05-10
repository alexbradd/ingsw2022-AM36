package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.server.model.exceptions.NoTowersException;
import it.polimi.ingsw.server.model.iterators.AssistantValueIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for StudentMovePhase
 */
class StudentMovePhaseTest {
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
        deckBob.add(new Assistant(AssistantType.OSTRICH, Mage.FAIRY));
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
                        .receiveTower(new Tower(TowerColor.WHITE, bob)));
        previous = new MockActionPhase(t, ann);
        avi = new AssistantValueIterator(t.getBoards(), 0);
        avi.next();
    }

    /**
     * Null check constructors
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> new StudentMovePhase(null, null, null));
        assertThrows(IllegalArgumentException.class, () -> new StudentMovePhase(previous, null, null));
        assertThrows(IllegalArgumentException.class, () -> new StudentMovePhase(previous, avi, null));
    }

    /**
     * Checks if the phase authorizes the correct player.
     */
    @Test
    void checkAuthorizePlayer() {
        StudentMovePhase phase = new StudentMovePhase(previous, avi, ann);

        assertThrows(InvalidPlayerException.class, () -> phase.authorizePlayer("bob"));
        assertAll(() -> assertEquals(phase.authorizePlayer("ann"), ann));
    }

    /**
     * Check that forbidden operations throw
     */
    @Test
    void checkOps() {
        StudentMovePhase p = new StudentMovePhase(previous, avi, ann);
        assertThrows(UnsupportedOperationException.class, () -> p.chooseMageDeck(ann, Mage.MAGE));
        assertThrows(UnsupportedOperationException.class, () -> p.drainCloud(bob, 0));
        assertThrows(UnsupportedOperationException.class, () -> p.moveMn(ann, 2));
        assertThrows(UnsupportedOperationException.class, () -> p.addPlayer("carl"));
        assertThrows(UnsupportedOperationException.class, () -> p.removePlayer("ann"));
        assertThrows(UnsupportedOperationException.class, () -> p.playAssistant(ann, AssistantType.CHEETAH));
    }

    /**
     * Test that markStudentMove allows for maximum 3 student moves before transitioning to MnMovePhase
     */
    @Test
    void markStudentMove() throws InvalidPhaseUpdateException {
        Phase moves = new StudentMovePhase(previous, avi, ann);
        for (int i = 0; i < 3; i++) {
            moves = moves.markStudentMove(ann);
        }
        Phase finalMoves = moves;
        assertThrows(UnsupportedOperationException.class, () -> finalMoves.markStudentMove(ann));
        assertInstanceOf(MnMovePhase.class, moves);
    }

    /**
     * Checks that we are immediately sent to EndGamePhase if somehow a player can exhaust all their towers
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
        Phase moves = new StudentMovePhase(new MockActionPhase(withoutTowers, ann), avi, ann);
        moves = moves.markStudentMove(ann);
        assertInstanceOf(EndgamePhase.class, moves);
    }

    /**
     * Checks that we are immediately sent to EndGamePhase if somehow only 3 islands remain in the game
     */
    @Test
    void immediateEndgameIslands() throws InvalidPhaseUpdateException {
        Table withoutIslands = t.updateIslandList(is -> List.of(new Island(1), new Island(2), new Island(3)));
        Phase moves = new StudentMovePhase(new MockActionPhase(withoutIslands, ann), avi, ann);
        moves = moves.markStudentMove(ann);
        assertInstanceOf(EndgamePhase.class, moves);
    }

    /**
     * Check that common attributes like hasPlayedCharacter, influenceCalculators or mxExtractors survive a phase change
     */
    @Test
    void sharedStateSurvival() throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Table withCharacter = t.updateCharacters(cs -> {
                    cs.add(new MockCharacter());
                    return cs;
                })
                .updateBoardOf(ann, b -> b.receiveCoin().receiveCoin());
        MockActionPhase mockActionPhase = new MockActionPhase(withCharacter, ann);
        ActionPhase phase = new StudentMovePhase(mockActionPhase, avi, ann);
        phase = (ActionPhase) phase.playCharacter(ann, CharacterType.HERBALIST, new CharacterStep[]{});
        phase = phase.setInfluenceCalculator(new ExtraPointsInfluenceDecorator(phase.getInfluenceCalculator(), ann, 2));
        phase = phase.setMaxExtractor(new EqualityInclusiveMaxExtractor(ann));

        for (int i = 0; i < 3; i++)
            phase = (ActionPhase) phase.markStudentMove(ann);

        assertInstanceOf(MnMovePhase.class, phase);
        assertInstanceOf(ExtraPointsInfluenceDecorator.class, phase.getInfluenceCalculator());
        assertInstanceOf(EqualityInclusiveMaxExtractor.class, phase.getMaxExtractor());
        assertTrue(phase.hasPlayedCharacter());
    }
}