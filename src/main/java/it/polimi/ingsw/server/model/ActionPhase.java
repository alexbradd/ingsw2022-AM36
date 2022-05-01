package it.polimi.ingsw.server.model;

/**
 * STUB
 */
public class ActionPhase extends IteratedPhase {
    private final boolean playedCharacter;
    private final Island extraInfluenceCalculation;
    private final MaxExtractor maximumExtractor;
    private final int extraMNMoves;

    ActionPhase(IteratedPhase prev, Player current) {
        super(prev, current);
        playedCharacter = false;
        extraInfluenceCalculation = null;
        maximumExtractor = null;
        extraMNMoves = 0;
    }

    ActionPhase(ActionPhase prev) {
        super(prev);
        playedCharacter = prev.playedCharacter;
        extraInfluenceCalculation = prev.extraInfluenceCalculation;
        maximumExtractor = prev.maximumExtractor;
        extraMNMoves = prev.extraMNMoves;
    }
}
