package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyPhaseTest {

    /**
     * Tests for the constructors.
     * Also tests that the shallow-copy constructor actually copies a LobbyPhase instance.
     */
    @Test
    @DisplayName("Constructors tests")
    void constructorTests() {
        assertThrows(IllegalArgumentException.class, () -> new LobbyPhase((GameParameters) null));
        LobbyPhase p = new LobbyPhase(GameParameters.twoPlayerGame(false));
        assertEquals(p, new LobbyPhase(p));
    }

    /**
     * Tests for the addPlayer(username) method.
     */
    @Nested
    @DisplayName("Tests for addPlayer(username) method")
    class AddPlayerTest {

        /**
         * Tests the behaviour of the method in case of incorrect parameters.
         */
        @Test
        @DisplayName("Incorrect parameter case")
        void incorrectParameterTest() {
            GameParameters parameters = GameParameters.twoPlayerGame(false);
            Phase p1 = new LobbyPhase(parameters);
            assertThrows(IllegalArgumentException.class, () -> p1.addPlayer(null));
        }

        /**
         * Tests the correct behaviour of the method for a 2-players game.
         */
        @Test
        @DisplayName("Two-players game test")
        void twoPlayersTest() {
            Phase p1, p2, p3; /*Phases*/
            Player player1 = new Player("player1"); /*Players*/
            Player player2 = new Player("player2");
            GameParameters parameters = GameParameters.twoPlayerGame(false);

            try {
                p1 = new LobbyPhase(parameters);
                assertTrue(p1.getTable().getPlayers().isEmpty());

                p2 = p1.addPlayer("player1");
                assertEquals(p2.getClass(), LobbyPhase.class);
                assertTrue(contains(p2, player1));
                assertTrue(isValidTwoPlayersColor(colorOf(p2, player1)));
                assertThrows(InvalidPhaseUpdateException.class, () -> p2.addPlayer("player1"));

                p3 = p2.addPlayer("player2");
                assertEquals(p3.getClass(), PreparePhase.class);
                assertTrue(contains(p3, player1) && contains(p3, player2));
                assertTrue(isValidTwoPlayersColor(colorOf(p3, player2)) && areDifferentColors(p3, player1, player2, null));
                assertThrows(UnsupportedOperationException.class, () -> p3.addPlayer("player1"));
                assertThrows(UnsupportedOperationException.class, () -> p3.addPlayer("player"));

                assertFalse(p1.equals(p2) || p1.equals(p3) || p2.equals(p3));

            } catch (InvalidPhaseUpdateException e) {
                e.printStackTrace();
            }



        }

        /**
         * Tests the correct behaviour of the method for a 3-players game.
         */
        @Test
        @DisplayName("Three-players game test")
        void threePlayersTest() {
            Phase p1, p2, p3, p4; /*Phases*/
            Player player1 = new Player("player1"); /*Players*/
            Player player2 = new Player("player2");
            Player player3 = new Player("player3");
            GameParameters parameters = GameParameters.threePlayersGame(false);

            try {
                p1 = new LobbyPhase(parameters);
                assertTrue(p1.getTable().getPlayers().isEmpty());

                p2 = p1.addPlayer("player1");
                assertEquals(p2.getClass(), LobbyPhase.class);
                assertTrue(contains(p2, player1));
                assertThrows(InvalidPhaseUpdateException.class, () -> p2.addPlayer("player1"));

                p3 = p2.addPlayer("player2");
                assertEquals(p3.getClass(), LobbyPhase.class);
                assertTrue(contains(p3, player1) && contains(p3, player2));
                assertTrue(areDifferentColors(p3, player1, player2, null));
                assertThrows(InvalidPhaseUpdateException.class, () -> p3.addPlayer("player2"));

                p4 = p3.addPlayer("player3");
                assertEquals(p4.getClass(), PreparePhase.class);
                assertTrue(contains(p4, player1) && contains(p4, player2) && contains(p4, player3));
                assertTrue(areDifferentColors(p4, player1, player2, player3));
                assertThrows(UnsupportedOperationException.class, () -> p4.addPlayer("player"));

                assertFalse(p1.equals(p2) || p1.equals(p3) || p2.equals(p3) || p4.equals(p3) || p4.equals(p2) || p4.equals(p1));

            } catch (InvalidPhaseUpdateException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Tests for the removePlayer(username) method.
     */
    @Nested
    @DisplayName("Tests for the removePlayer(username) method")
    class RemovePlayerTest {

        /**
         * Tests the correct behaviour of the method in case of a {@code null} parameter.
         */
        @Test
        @DisplayName("Incorrect parameter case")
        void incorrectParameterTest() {
            GameParameters parameters = GameParameters.twoPlayerGame(false);
            Phase p = new LobbyPhase(parameters);
            assertThrows(IllegalArgumentException.class, () -> p.removePlayer(null));
        }

        /**
         * Test the correct behaviour of the method with a 2-players game.
         */
        @Test
        @DisplayName("Correct behaviour")
        void removeTest() {
            Phase p, p1, p2; /*Phases*/
            Player player = new Player("player"); /*Player*/
            GameParameters parameters = GameParameters.twoPlayerGame(false);

            try {
                p1 = new LobbyPhase(parameters);
                p2 = p1.addPlayer("player");
                p = p2.removePlayer("player");

                assertEquals(p.getClass(), LobbyPhase.class);
                assertEquals(p1, p);
                assertTrue(contains(p2, player) && !contains(p, player));

                assertThrows(InvalidPhaseUpdateException.class, () -> p2.removePlayer("incorrect_player"));
                assertThrows(InvalidPhaseUpdateException.class, () -> p.removePlayer(""));

            } catch (InvalidPhaseUpdateException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Helper method that returns whether a certain {@code player} is contained inside the table of
     * a certain {@code phase}.
     *
     * @param phase the phase
     * @param player the player
     * @return {@code true} if the phase's table contains the player, {@code false} otherwise
     */
    private boolean contains(Phase phase, Player player) {
        return phase.getTable().getPlayers().contains(player);
    }

    /**
     * Helper method that returns the tower color of the given player in a certain phase.
     * Assumes that the player is inside the table of the given phase.
     *
     * @param phase the phase
     * @param player the player
     * @return the tower color of the given player
     */
    private TowerColor colorOf(Phase phase, Player player) {
        return phase.getTable().getBoardOf(player).getTowersColor();
    }

    /**
     * Helper method that checks whether the given color is valid in a two-players game.
     *
     * @param color the given color
     * @return {@code true} if {@code color != TowerColor.GRAY}, {@code false} otherwise
     */
    private boolean isValidTwoPlayersColor(TowerColor color) {
        return color != TowerColor.GRAY;
    }

    /**
     * Helper method that checks if the tower-colors associated with each player are different.
     * The {@code player3} argument could be null, in this case we check if the colors associated with player1
     * and player2 are different.
     *
     * @param phase the phase
     * @param player1 the first player
     * @param player2 the second player
     * @param player3 the third player (could be {@code null})
     * @return {@code true} if the colors are different, {@code false} otherwise
     */
    private boolean areDifferentColors(Phase phase, Player player1, Player player2, Player player3) {
        if(player3 == null) {
            return colorOf(phase, player1) != colorOf(phase, player2);
        }
        else {
            return colorOf(phase, player1) != colorOf(phase, player2) &&
                   colorOf(phase, player1) != colorOf(phase, player3) &&
                   colorOf(phase, player2) != colorOf(phase, player3);
        }
    }


}
