package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.server.model.iterators.AssistantValueIterator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for MnMovePhase
 */
class MnMovePhaseTest {
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
                    b = b.receiveDeck(deckAnn).playAssistant(AssistantType.CHEETAH);
                    for (int i = 0; i < 8; i++)
                        b = b.receiveTower(new Tower(TowerColor.BLACK, ann));
                    return b;
                })
                .updateBoardOf(bob, b -> {
                    b = b.receiveDeck(deckBob).playAssistant(AssistantType.OSTRICH);
                    for (int i = 0; i < 8; i++)
                        b = b.receiveTower(new Tower(TowerColor.WHITE, bob));
                    return b;
                })
                .updateSack(s -> s.add(new Student(annColor)));
        previous = new MockActionPhase(t, ann);
        avi = new AssistantValueIterator(t.getBoards(), 0);
        avi.next();
    }

    /**
     * Creates the values for {@link #testScrub(ScrubTester)}. List configuration tested are:
     *
     * <ol>
     *     <li>0 1 2 [3,4,5] [6,7] 8 9 10 11</li>
     *     <li>[0,1,2] [3,4] 5 6 7 8 9 10 11</li>
     *     <li>0 1 2 3 4 5 6 [7,8,9] [10,11]</li>
     *     <li>0,1] [2,3] 4 5 6 7 8 9 10 [11</li>
     * </ol>
     *
     * @see ScrubTester
     */
    static Stream<ScrubTester> scrubValueSource() {
        return Stream.of(
                // 0 1 2 [3,4,5] [6,7] 8 9 10 11
                new ScrubTester(new int[]{3, 4, 5}, new int[]{6, 7}, 3, 4, 9),
                // [0,1,2] [3,4] 5 6 7 8 9 10 11
                new ScrubTester(new int[]{0, 1, 2}, new int[]{3, 4}, 0, 1, 9),
                // 0 1 2 3 4 5 6 [7,8,9] [10,11]
                new ScrubTester(new int[]{7, 8, 9}, new int[]{10, 11}, 7, 8, 0),
                // 0,1] [2,3] 4 5 6 7 8 9 10 [11
                new ScrubTester(new int[]{11, 0, 1}, new int[]{2, 3}, 0, 1, 5)
        );
    }

    /**
     * Checks that null is rejected.
     */
    @Test
    void nullCheck() {
        assertThrows(IllegalArgumentException.class, () -> new MnMovePhase(null, null));
        assertThrows(IllegalArgumentException.class, () -> new MnMovePhase(previous, null));
        assertThrows(IllegalArgumentException.class, () -> new MnMovePhase(previous, avi).moveMn(null, 0));
    }

    /**
     * Bound check moveMn()
     */
    @Test
    void boundCheckMovement() {
        MnMovePhase phase = new MnMovePhase(previous, avi);
        assertThrows(InvalidPhaseUpdateException.class, () -> phase.moveMn(ann, -1));
        assertThrows(InvalidPhaseUpdateException.class, () -> phase.moveMn(ann, 0));
    }

    /**
     * Check that forbidden operations throw
     */
    @Test
    void checkOps() {
        MnMovePhase p = new MnMovePhase(previous, avi);
        assertThrows(UnsupportedOperationException.class, () -> p.chooseMageDeck(ann, Mage.MAGE));
        assertThrows(UnsupportedOperationException.class, () -> p.drainCloud(ann, 0));
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
        MnMovePhase phase = new MnMovePhase(previous, avi);

        assertThrows(InvalidPlayerException.class, () -> phase.authorizePlayer("bob"));
        assertAll(() -> assertEquals(phase.authorizePlayer("ann"), ann));
    }

    /**
     * Checks if the phase moves MN for the allowed number of steps.
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})
    void checkMovement(int start) throws InvalidPhaseUpdateException {
        Table fixedMn = t.updateMotherNature(m -> new MotherNature(t.getIslandList(), start));
        MnMovePhase phase = new MnMovePhase(new MockActionPhase(fixedMn, ann), avi);
        Island pre = phase.getTable().getMotherNature().getCurrentIsland();

        assertThrows(InvalidPhaseUpdateException.class, () -> phase.moveMn(ann, -5));
        assertThrows(InvalidPhaseUpdateException.class, () -> phase.moveMn(ann, 0));
        assertThrows(InvalidPhaseUpdateException.class, () -> phase.moveMn(ann, 456));

        Phase afterMove = phase.moveMn(ann, 1);
        Island post = afterMove.getTable().getMotherNature().getCurrentIsland();
        assertInstanceOf(CloudPickPhase.class, afterMove);
        assertNotEquals(pre, post);
        if (post.getIds().get(0) == 0)
            assertEquals(11, pre.getIds().get(0));
        else
            assertEquals(post.getIds().get(0) - 1, pre.getIds().get(0));
    }

    /**
     * Checks if the move extension is honored
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})
    void checkMovementExtension(int start) {
        Table fixedMn = t.updateMotherNature(m -> new MotherNature(t.getIslandList(), start));
        MnMovePhase phase = (MnMovePhase) new MnMovePhase(new MockActionPhase(fixedMn, ann), avi)
                .requestExtraMnMovement(2);

        assertThrows(InvalidPhaseUpdateException.class, () -> phase.moveMn(ann, -5));
        assertThrows(InvalidPhaseUpdateException.class, () -> phase.moveMn(ann, 0));
        assertThrows(InvalidPhaseUpdateException.class, () -> phase.moveMn(ann, 456));
        assertAll(() -> phase.moveMn(ann, 1));
        assertAll(() -> phase.moveMn(ann, 2));
        assertAll(() -> phase.moveMn(ann, 3));
    }

    /**
     * Checks if towers are assigned and if we transitioned to CloudPickPhase
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})
    void checkAssignTower(int start) throws InvalidPhaseUpdateException {
        int nextId = start >= 11 ? 0 : start + 1;
        Table withStudents = t
                .updateMotherNature(m -> new MotherNature(t.getIslandList(), start))
                .updateIslandList(is -> is.stream()
                        .map(i -> {
                            if (i.getIds().get(0) == nextId)
                                return i.updateStudents(ss -> ss.add(new Student(annColor)));
                            return i;
                        })
                        .toList());
        MnMovePhase phase = new MnMovePhase(new MockActionPhase(withStudents, ann), avi);
        Phase afterMove = phase.moveMn(ann, 1);
        assertInstanceOf(CloudPickPhase.class, afterMove);
        assertEquals(7, afterMove.getTable().getBoardOf(ann).getNumOfTowers());
        afterMove.getTable().getIslandList().forEach(i -> {
            if (i.getIds().get(0) == nextId)
                assertEquals(1, i.getNumOfTowers());
        });
    }

    /**
     * Checks if only the available amount of Towers is sent and then that we are sent to a EndGamePhase
     */
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11})
    void checkAssignMaxTowers(int start) throws InvalidPhaseUpdateException {
        int next1Id = start >= 11 ? 0 : start + 1;
        int next2Id = next1Id >= 11 ? 1 : next1Id + 1;
        Table mergedWithAnnWithOnlyOneTower = t
                .updateMotherNature(m -> new MotherNature(t.getIslandList(), start))
                .updateIslandList(is -> {
                    Island next1 = is.get(next1Id);
                    Island next2 = is.get(next2Id);
                    Island merge = next1.updateTowers(ts -> {
                                ts.add(new Tower(TowerColor.WHITE, bob));
                                return ts;
                            })
                            .merge(next2.updateTowers(ts -> {
                                ts.add(new Tower(TowerColor.WHITE, bob));
                                return ts;
                            }))
                            .updateStudents(c ->
                                    c.add(new Student(annColor))
                                            .add(new Student(annColor))
                                            .add(new Student(annColor))
                                            .add(new Student(annColor)));
                    is.remove(next1);
                    is.remove(next2);
                    switch (start) {
                        case 9 -> is.add(merge);
                        case 10, 11 -> is.add(0, merge);
                        default -> is.add(start + 1, merge);
                    }
                    return is;
                })
                .updateBoardOf(ann, b -> {
                    for (int i = 0; i < 7; i++)
                        b = b.sendTower().getFirst();
                    return b;
                })
                .updateBoardOf(bob, b -> {
                    for (int i = 0; i < 2; i++)
                        b = b.sendTower().getFirst();
                    return b;
                });
        Phase phase = new MnMovePhase(new MockActionPhase(mergedWithAnnWithOnlyOneTower, ann), avi)
                .moveMn(ann, 1);
        assertInstanceOf(EndgamePhase.class, phase);
        assertEquals(0, phase.getTable().getBoardOf(ann).getNumOfTowers());
        switch (start) {
            case 10, 11 -> assertEquals(1, phase.getTable().getIslandList().get(0).getNumOfTowers());
            default -> assertEquals(1, phase.getTable().getIslandList().get(start + 1).getNumOfTowers());
        }
    }

    /**
     * Checks that we are immediately sent to EndGamePhase if somehow only 3 islands remain in the game
     */
    @Test
    void immediateEndgameIslands() throws InvalidPhaseUpdateException {
        List<Island> newList = List.of(new Island(1), new Island(2), new Island(3));
        Table withoutIslands = t
                .updateIslandList(is -> newList)
                .updateMotherNature(mn -> new MotherNature(newList, 0));
        Phase moves = new MnMovePhase(new MockActionPhase(withoutIslands, ann), avi);
        moves = moves.moveMn(ann, 1);
        assertInstanceOf(EndgamePhase.class, moves);
    }

    /**
     * Checks if list is scrubbed correctly.
     * <p>
     * Case 1: merging islands in the middle of the list
     * Case 2: merging islands at the beginning of the list
     * Case 3: merging islands at the end of the list
     * Case 4: merging last and first
     *
     * @see #scrubValueSource()
     */
    @ParameterizedTest
    @MethodSource("scrubValueSource")
    void testScrub(ScrubTester p) throws InvalidPhaseUpdateException {
        Table unmergedInMiddleOfList = t
                .updateMotherNature(m -> new MotherNature(t.getIslandList(), p.mnStart))
                .updateIslandList(is -> is.stream().map(i -> {
                            if (i.getIds().get(0) == p.annP[0] || i.getIds().get(0) == p.annP[1] || i.getIds().get(0) == p.annP[2])
                                return i.updateTowers(t -> {
                                    t.add(new Tower(TowerColor.BLACK, ann));
                                    return t;
                                });
                            if (i.getIds().get(0) == p.bobP[0] || i.getIds().get(0) == p.bobP[1])
                                return i.updateTowers(t -> {
                                    t.add(new Tower(TowerColor.WHITE, bob));
                                    return t;
                                });
                            // Necessary to force student calculation: if no maximum influence is found, MN will leave
                            // the list as is since during normal gameplay no maximum influence -> no towers -> no
                            // change in islands.
                            if (i.getIds().get(0) == p.mnStart + 1)
                                return i.updateStudents(c -> c.add(new Student(annColor)));
                            return i;
                        })
                        .toList());
        Phase afterMove = new MnMovePhase(new MockActionPhase(unmergedInMiddleOfList, ann), avi).moveMn(ann, 1);
        assertInstanceOf(CloudPickPhase.class, afterMove);

        List<Island> scrubbedList = afterMove.getTable().getIslandList();
        assertEquals(9, scrubbedList.size());

        assertEquals(3, scrubbedList.get(p.annI).getIds().size());
        assertEquals(scrubbedList.get(p.annI).getIds(), List.of(p.annP[0], p.annP[1], p.annP[2]));
        assertTrue(scrubbedList.get(p.annI).getControllingPlayer().isPresent());
        assertEquals(ann, scrubbedList.get(p.annI).getControllingPlayer().get());

        assertEquals(2, scrubbedList.get(p.bobI).getIds().size());
        assertEquals(scrubbedList.get(p.bobI).getIds(), List.of(p.bobP[0], p.bobP[1]));
        assertTrue(scrubbedList.get(p.bobI).getControllingPlayer().isPresent());
        assertEquals(bob, scrubbedList.get(p.bobI).getControllingPlayer().get());
    }

    /**
     * Check if blocks are honored and removed
     */
    @Test
    void testBlockHonoredThenRemoved() throws InvalidPhaseUpdateException {
        Table withBlockedIsland = t
                .updateMotherNature(m -> new MotherNature(t.getIslandList(), 0))
                .updateCharacters(cs -> {
                    cs.add(new Herbalist());
                    return cs;
                })
                .updateIslandList(is -> is.stream().map(i -> {
                    if (i.getIds().get(0) == 1)
                        return i.pushBlock(new BlockCard(CharacterType.HERBALIST))
                                .updateStudents(c -> c.add(new Student(annColor)));
                    return i;
                }).toList());
        Phase afterMove = new MnMovePhase(new MockActionPhase(withBlockedIsland, ann), avi).moveMn(ann, 1);
        Table afterMoveTable = afterMove.getTable();
        assertEquals(8, afterMoveTable.getBoardOf(ann).getNumOfTowers());
        assertFalse(afterMoveTable.getIslandList().get(1).isBlocked());
        assertTrue(afterMoveTable.getIslandList().get(1).getControllingPlayer().isEmpty());
    }

    /**
     * Check that if no students are left in the sack and there is a next Player, we transition to StudentMovePhase
     */
    @Test
    void studentMoveNoStudentsInSack() throws InvalidPhaseUpdateException {
        Table withoutStudents = t.updateSack(s -> s.remove().getFirst());
        Phase afterMove = new MnMovePhase(new MockActionPhase(withoutStudents, ann), avi).moveMn(ann, 1);

        assertInstanceOf(StudentMovePhase.class, afterMove);
        assertEquals(bob, ((StudentMovePhase) afterMove).getCurrentPlayer());
    }

    /**
     * Check that if no students are left in the sack and there isn't a next Player, we transition to EndgamePhase
     */
    @Test
    void endgameNoStudentsInSack() throws InvalidPhaseUpdateException {
        Table withoutStudents = t.updateSack(s -> s.remove().getFirst());
        AssistantValueIterator last = new AssistantValueIterator(avi);
        last.next();
        Phase afterMove = new MnMovePhase(new MockActionPhase(withoutStudents, ann), last).moveMn(bob, 1);

        assertInstanceOf(EndgamePhase.class, afterMove);
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
        ActionPhase phase = new MnMovePhase(mockActionPhase, avi);
        phase = (ActionPhase) phase.playCharacter(ann, CharacterType.HERBALIST, new CharacterStep[]{});
        phase = phase.setInfluenceCalculator(new ExtraPointsInfluenceDecorator(phase.getInfluenceCalculator(), ann, 2));
        phase = phase.setMaxExtractor(new EqualityInclusiveMaxExtractor(ann));

        phase = (ActionPhase) phase.moveMn(ann, 1);

        assertInstanceOf(CloudPickPhase.class, phase);
        assertInstanceOf(ExtraPointsInfluenceDecorator.class, phase.getInfluenceCalculator());
        assertInstanceOf(EqualityInclusiveMaxExtractor.class, phase.getMaxExtractor());
        assertTrue(phase.hasPlayedCharacter());
    }

    /**
     * Data holder for the test data in {@link MnMovePhaseTest#testScrub(ScrubTester)}.
     */
    private static class ScrubTester {
        /**
         * indexes of the group conquered by the first player (in order), size 3
         */
        public int[] annP;
        /**
         * indexes of the group conquered by the second player (in order), size 2
         */
        public int[] bobP;
        /**
         * index to check after scrub for the first player
         */
        public int annI;
        /**
         * index to check after scrub for the first player
         */
        public int bobI;
        /**
         * Starting index for MN
         */
        public int mnStart;

        /**
         * Autogenerated constructor with all public fields
         */
        public ScrubTester(int[] annP, int[] bobP, int annI, int bobI, int mnStart) {
            this.annP = annP;
            this.bobP = bobP;
            this.annI = annI;
            this.bobI = bobI;
            this.mnStart = mnStart;
        }
    }
}