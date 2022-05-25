package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.CharacterType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for Phase. Since Phase is abstract, methods are tested through a mock phase declared below.
 *
 * @see MockPhase
 */
public class PhaseTest {
    /**
     * Test that all methods throw exceptions
     */
    @Test
    void assertNotImplementedException() {
        MockPhase p = new MockPhase(GameParameters.twoPlayerGame(true));
        assertThrows(UnsupportedOperationException.class, p::getTable);
        assertThrows(UnsupportedOperationException.class, () -> p.authorizePlayer(null));
        assertThrows(UnsupportedOperationException.class, () -> p.chooseMageDeck(null, null));
        assertThrows(UnsupportedOperationException.class, () -> p.drainCloud(null, 0));
        assertThrows(UnsupportedOperationException.class, () -> p.markStudentMove(null));
        assertThrows(UnsupportedOperationException.class, () -> p.moveMn(null, 0));
        assertThrows(UnsupportedOperationException.class, () -> p.addPlayer(null));
        assertThrows(UnsupportedOperationException.class, () -> p.removePlayer(null));
        assertThrows(UnsupportedOperationException.class, () -> p.playAssistant(null, AssistantType.CHEETAH));
        assertThrows(UnsupportedOperationException.class, () -> p.playCharacter(null, CharacterType.HERBALIST));
        assertThrows(UnsupportedOperationException.class, () -> p.getFromEntrance(null, null));
        assertThrows(UnsupportedOperationException.class, () -> p.addToHall(null, null));
        assertThrows(UnsupportedOperationException.class, () -> p.addToIsland(null, 0, null));
    }

    /**
     * Mock a concrete phase implementation
     */
    private static class MockPhase extends Phase {

        MockPhase(GameParameters parameters) {
            super(parameters);
        }

        MockPhase(Phase old) {
            super(old);
        }
    }
}
