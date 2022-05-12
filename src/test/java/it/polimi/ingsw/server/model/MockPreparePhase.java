package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.enums.TowerColor;

import java.util.Arrays;

/**
 * Mocks an instance of Prepare phase. Used in testing characters
 */
public class MockPreparePhase extends PreparePhase {
    public MockPreparePhase() {
        super(new MockPhase(new Table()
                .addPlayer(new Player("ann"), 7, 8, TowerColor.BLACK)
                .addPlayer(new Player("bob"), 7, 8, TowerColor.WHITE)));
    }

    @Override
    Tuple<PreparePhase, Student> extractFromSack() {
        return new Tuple<>(
                new MockPreparePhase(),
                new Student(Arrays.stream(PieceColor.values()).findAny().orElseThrow())
        );
    }
}
