package it.polimi.ingsw.server.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * Unit test for the Game class.
 *
 * @author Leonardo Bianconi
 * @see Game
 */
public class LobbyPhaseTest {

    /**
     * Test 1: a new 3 player game is created and many players connect to the lobby. It then checks the number of
     * players after each connection/disconnection. Commented to illustrate the process of creation and run of new games.
     */
    @Test
    public void gameLobbyTest() {
        Runnable mainThread = new Runnable() {
            @Override
            public void run() {
                // a game is created by the main thread
                Game g = GameFactory.threePlayerGame(false);

                PlayerJoinEvent a = new PlayerJoinEvent("anna");
                PlayerLeaveEvent t = new PlayerLeaveEvent("trudy");
                PlayerJoinEvent b = new PlayerJoinEvent("bob");
                PlayerJoinEvent a1 = new PlayerJoinEvent("anna");
                PlayerLeaveEvent br = new PlayerLeaveEvent("bob");
                PlayerJoinEvent c = new PlayerJoinEvent("carl");
                PlayerJoinEvent d = new PlayerJoinEvent("donny");
                PlayerJoinEvent e = new PlayerJoinEvent("earl");

                // the game is now started as a new thread by the main thread
                g.start();

                /* join/leave commands
                   expected:    anna joins
                                trudy tries to leave -> exception, not in game
                                bob joins
                                anna tries to join -> exception
                                bob leaves
                                carl joins
                                donny joins
                                earl tries to join -> exception
                */
                g.consumeUserEvent(a);
                assertEquals(1, g.getnPlayers());
                g.consumeUserEvent(t);
                assertEquals(1, g.getnPlayers());
                g.consumeUserEvent(b);
                assertEquals(2, g.getnPlayers());
                g.consumeUserEvent(a1);
                assertEquals(2, g.getnPlayers());
                g.consumeUserEvent(br);
                assertEquals(1, g.getnPlayers());
                g.consumeUserEvent(c);
                assertEquals(2, g.getnPlayers());
                g.consumeUserEvent(d);
                assertEquals(3, g.getnPlayers());
                g.consumeUserEvent(e);
                assertEquals(3, g.getnPlayers());
            }

            ;
        };
    }

    /**
     * Test 2: a new 2 player game is created and 3 players connect to the lobby. It then checks whether the game starts
     * or not. Commented to illustrate the process of creation and run of new games.
     */
    @Test
    public void gameLobbyTest2() {
        Runnable mainThread = new Runnable() {
            @Override
            public void run() {
                // a game is created by the main thread
                Game g = GameFactory.threePlayerGame(false);
            }
        };
    }
}
