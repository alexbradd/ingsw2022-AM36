package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.*;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link PreparePhase}
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PreparePhaseTest {
    /**
     * The instance of the {@link PreparePhase} to be tested.
     */
    private Phase prepare;

    private Player p1, p2;

    @Test
    @DisplayName("Test for the constructors")
    @BeforeAll
    void constructorTest() {
        p1 = new Player("Alice");
        p2 = new Player("Bob");

        try {
            prepare = new LobbyPhase(GameParameters.twoPlayerGame(true)).addPlayer(p1.getUsername())
                    .addPlayer(p2.getUsername());


        } catch (InvalidPhaseUpdateException e) {
            e.printStackTrace();
        }

        // tests with null values
        PreparePhase nullPrepare = null;

        assertThrows(IllegalArgumentException.class,
                () -> new PreparePhase(nullPrepare));
    }


    @Test
    @DisplayName("Test unsupported operations")
    void operationNotSupportedTest() {
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.addPlayer("test"));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.removePlayer("test"));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.playAssistant(p1, AssistantType.CHEETAH));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.markStudentMove(p1));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.addPlayer("test"));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.drainCloud(p1, 0));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.addPlayer("test"));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.playCharacter(p1, CharacterType.HERBALIST, new CharacterStep[1]));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.moveMn(p1, 1));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.updateHall(p1, hall -> hall = hall.add(new Student(PieceColor.RED))));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.updateEntrance(p1, e -> e = e.add(new Student(PieceColor.RED))));
        assertThrows(UnsupportedOperationException.class,
                () -> prepare.updateIsland(p1, 0, i -> i = i.add(new Student(PieceColor.RED))));
    }

    @Test
    @DisplayName("Basic test for picking a mage deck, then checking if the table is ready for the game.")
    void chooseMageTest() {
        try {
            assertEquals(2, prepare.getTable().getPlayers().size());
            prepare = prepare.chooseMageDeck(p1, Mage.FAIRY);

            assertEquals(PreparePhase.class, prepare.getClass());
            assertTrue(prepare.getTable().getBoardOf(p1).getLastPlayedAssistant().isEmpty());
            assertEquals(prepare.getTable().getBoardOf(p1).getAssistants().get(0).getMage(), Mage.FAIRY);
            assertEquals(10, prepare.getTable().getBoardOf(p1).getAssistants().size());

            assertThrows(IndexOutOfBoundsException.class, () ->
                    prepare.getTable().getBoardOf(p2).getAssistants().get(0));

            assertEquals(PreparePhase.class, prepare.getClass());

            assertThrows(UnsupportedOperationException.class,
                    () -> prepare.chooseMageDeck(p1, Mage.MAGE));

            assertThrows(IllegalArgumentException.class,
                    () -> prepare.chooseMageDeck(p2, null));

            assertThrows(InvalidPhaseUpdateException.class,
                    () -> prepare.chooseMageDeck(p2, Mage.FAIRY));

            Phase planning = prepare.chooseMageDeck(p2, Mage.MAGE);
            assertEquals(planning.getTable().getBoardOf(p2).getAssistants().get(0).getMage(), Mage.MAGE);
            assertEquals(PlanningPhase.class, planning.getClass());

            // assertions regarding the Table
            // ISLANDS
            List<Island> noStudents = planning.getTable().getIslandList()
                    .stream()
                    .filter(i -> i.getStudents().isEmpty())
                    .toList();

            List<Island> withStudents = planning.getTable().getIslandList()
                    .stream()
                    .filter(i -> i.getStudents().size() >= 1)
                    .toList();

            for (Island i : withStudents)
                assertEquals(1, i.getStudents().size());

            assertEquals(planning.parameters.getnIslands(), noStudents.size() + withStudents.size());
            assertEquals(2, noStudents.size());
            assertEquals(planning.parameters.getnIslands() - 2, withStudents.size());

            //SACK
            int nStudentsInSack = planning.parameters.getnOfProfessors() *
                    (planning.parameters.getnStudentsOfColor() - planning.parameters.getnStudentsInSack())
                    - planning.parameters.getnPlayers() * planning.parameters.getnStudentsEntrance()
                    - planning.parameters.getnPlayers() * planning.parameters.getnStudentsMovable();
            assertEquals(nStudentsInSack, planning.getTable().getSack().size());

            // CLOUDS
            assertEquals(planning.parameters.getnPlayers(), planning.getTable().getClouds().size());

            for (Cloud c : prepare.getTable().getClouds())
                assertEquals(0, c.getStudents().size());

            // STUDENTS ON BOARDS
            for (Board b : planning.getTable().getBoards())
                assertEquals(planning.parameters.getnStudentsEntrance(), b.getEntrance().size());

            // TOWERS ON BOARDS
            for (Board b : planning.getTable().getBoards()) {
                assertEquals(planning.parameters.getnTowers(), b.getNumOfTowers());
            }

            // CHARACTERS
            assertEquals(3, planning.getTable().getCharacters().size());


        } catch (InvalidPhaseUpdateException e) {
            e.printStackTrace();
        }
    }
}

