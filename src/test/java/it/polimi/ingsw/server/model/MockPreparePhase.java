package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;

import java.util.Arrays;
import java.util.Optional;

/**
 * Mocks an instance of Prepare phase. Used in testing characters
 */
public class MockPreparePhase extends PreparePhase {
    public MockPreparePhase() {
        super(new LobbyPhase(GameParameters.twoPlayerGame(true)));
    }

    @Override
    Tuple<PreparePhase, Student> extractFromSack() {
        return new Tuple<>(
                new MockPreparePhase(),
                new Student(Arrays.stream(PieceColor.values()).findAny().orElseThrow())
        );
    }
}
