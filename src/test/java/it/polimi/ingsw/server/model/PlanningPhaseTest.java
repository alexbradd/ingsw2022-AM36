package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link PlanningPhase}.
 */
class PlanningPhaseTest {
    /**
     * The instance of the {@link PlanningPhase} to be tested.
     */
    private Phase plan;

    /**
     * A player playing the test game.
     */
    private Player p1, p2, p3;

    @Nested
    @DisplayName("Two player game")
    class TwoPlayerGame {

        @Test
        @DisplayName("Test for the constructors")
        @BeforeEach
        void constructorTest() {
            p1 = new Player("Alice");
            p2 = new Player("Bob");

            try {
                plan = new LobbyPhase(GameParameters.twoPlayerGame(false)).addPlayer(p1.getUsername())
                        .addPlayer(p2.getUsername())
                        .chooseMageDeck(p1, Mage.FAIRY)
                        .chooseMageDeck(p2, Mage.MAGE);

            } catch (InvalidPhaseUpdateException e) {
                e.printStackTrace();
            }

            assertEquals(PlanningPhase.class, plan.getClass());
            assertEquals(p1, ((PlanningPhase) plan).getCurrentPlayer());

            for (Cloud c : plan.getTable().getClouds()) {
                assertEquals(plan.parameters.getnStudentsMovable(), c.getStudents().size());
            }

            // tests with null values
            PlanningPhase nullPlan = null;
            assertThrows(IllegalArgumentException.class,
                    () -> new PlanningPhase(nullPlan));
        }

        @Test
        @DisplayName("Test operations not supported")
        void operationNotSupportedTest() {
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.addPlayer("test"));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.removePlayer("test"));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.chooseMageDeck(p1, Mage.MAGE));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.markStudentMove(p1));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.addPlayer("test"));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.drainCloud(p1, 0));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.addPlayer("test"));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.playCharacter(p1, CharacterType.HERBALIST));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.moveMn(p1, 1));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.addToHall(p1, new Student(PieceColor.RED)));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.getFromEntrance(p1, PieceColor.PINK));
            assertThrows(UnsupportedOperationException.class,
                    () -> plan.addToIsland(p1, 0, new Student(PieceColor.RED)));
        }

        @Test
        @DisplayName("Basic test for assistant picking (2 players)")
        void pickAssistantTest() {
            try {
                plan = plan.playAssistant(p1, AssistantType.CHEETAH);

                assertTrue(plan.getTable().getBoardOf(p1).getLastPlayedAssistant().isPresent());
                assertEquals(plan.getTable().getBoardOf(p1).getLastPlayedAssistant().get().getAssistantType(), AssistantType.CHEETAH);
                assertEquals(PlanningPhase.class, plan.getClass());

                assertThrows(UnsupportedOperationException.class,
                        () -> plan = plan.playAssistant(p1, AssistantType.OSTRICH));

                assertThrows(IllegalArgumentException.class,
                        () -> plan.playAssistant(p2, null));

                assertThrows(InvalidPhaseUpdateException.class,
                        () -> plan.playAssistant(p2, AssistantType.CHEETAH));

                Phase studentMove = plan.playAssistant(p2, AssistantType.OSTRICH);

                assertEquals(StudentMovePhase.class, studentMove.getClass());
                assertTrue(studentMove.getTable().getBoardOf(p2).getLastPlayedAssistant().isPresent());
                assertEquals(studentMove.getTable().getBoardOf(p2).getLastPlayedAssistant().get().getAssistantType(), AssistantType.OSTRICH);
            } catch (InvalidPhaseUpdateException e) {
                e.printStackTrace();
            }
        }
    }

    @Nested
    @DisplayName("Three player game")
    class ThreePlayerGame {

        @Test
        @DisplayName("Test for the constructors")
        @BeforeEach
        void constructorTest() {
            p1 = new Player("Alice");
            p2 = new Player("Bob");
            p3 = new Player("Carl");

            try {
                plan = new LobbyPhase(GameParameters.threePlayersGame(true))
                        .addPlayer(p1.getUsername())
                        .addPlayer(p2.getUsername())
                        .addPlayer(p3.getUsername())
                        .chooseMageDeck(p1, Mage.FAIRY)
                        .chooseMageDeck(p2, Mage.MAGE)
                        .chooseMageDeck(p3, Mage.KING);

            } catch (InvalidPhaseUpdateException e) {
                e.printStackTrace();
            }

            assertEquals(PlanningPhase.class, plan.getClass());
            assertEquals(p1, ((PlanningPhase) plan).getCurrentPlayer());

            for (Cloud c : plan.getTable().getClouds()) {
                assertEquals(plan.parameters.getnStudentsMovable(), c.getStudents().size());
            }

            // tests with null values
            PlanningPhase nullPlan = null;
            assertThrows(IllegalArgumentException.class,
                    () -> new PlanningPhase(nullPlan));
        }

        @Test
        @DisplayName("Basic test for assistant picking (3 players)")
        void pickAssistantTest() {
            try {
                plan = plan.playAssistant(p1, AssistantType.CHEETAH);

                assertEquals(PlanningPhase.class, plan.getClass());

                assertThrows(UnsupportedOperationException.class,
                        () -> plan = plan.playAssistant(p1, AssistantType.OSTRICH));

                assertThrows(UnsupportedOperationException.class,
                        () -> plan.playAssistant(p3, AssistantType.CHEETAH));

                assertThrows(IllegalArgumentException.class,
                        () -> plan.playAssistant(p2, null));

                assertThrows(InvalidPhaseUpdateException.class,
                        () -> plan.playAssistant(p2, AssistantType.CHEETAH));

                plan = plan.playAssistant(p2, AssistantType.OSTRICH);

                assertEquals(PlanningPhase.class, plan.getClass());

                assertThrows(UnsupportedOperationException.class,
                        () -> plan = plan.playAssistant(p1, AssistantType.OSTRICH));

                assertThrows(UnsupportedOperationException.class,
                        () -> plan = plan.playAssistant(p2, AssistantType.OSTRICH));

                assertThrows(IllegalArgumentException.class,
                        () -> plan.playAssistant(p3, null));

                assertThrows(InvalidPhaseUpdateException.class,
                        () -> plan.playAssistant(p3, AssistantType.CHEETAH));

                Phase studentMove = plan.playAssistant(p3, AssistantType.CAT);
                assertEquals(StudentMovePhase.class, studentMove.getClass());
                assertTrue(studentMove.getTable().getBoardOf(p3).getLastPlayedAssistant().isPresent());
                assertEquals(studentMove.getTable().getBoardOf(p3).getLastPlayedAssistant().get().getAssistantType(), AssistantType.CAT);
                assertTrue(studentMove.getTable().getBoardOf(p2).getLastPlayedAssistant().isPresent());
                assertEquals(studentMove.getTable().getBoardOf(p2).getLastPlayedAssistant().get().getAssistantType(), AssistantType.OSTRICH);
                assertTrue(studentMove.getTable().getBoardOf(p1).getLastPlayedAssistant().isPresent());
                assertEquals(studentMove.getTable().getBoardOf(p1).getLastPlayedAssistant().get().getAssistantType(), AssistantType.CHEETAH);


            } catch (InvalidPhaseUpdateException e) {
                e.printStackTrace();
            }
        }
    }
}