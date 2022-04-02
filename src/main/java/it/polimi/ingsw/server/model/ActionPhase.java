package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;

class ActionPhase {
    public Hall getPlayerHall() {
        return null;
    }

    public Entrance getPlayerEntrance() {
        return null;
    }

    public void swapStudents(PieceColor c1, PieceColor c2, BidirectionalStudentMove a, BidirectionalStudentMove b) {

    }

    public InfluenceCalculator getInfluenceCalculator() {
        return null;
    }

    public void setInfluenceCalculator(InfluenceCalculator c) {

    }

    public Island getIsland(int islandIndex) {
        return null;
    }

    public void requestExtraInfluenceCalculation(Island island) {
    }

    public Player getCurrentPlayer() {
        return null;
    }

    public void setMaximumExtractor(EqualityInclusiveMaxExtractor equalityInclusiveMaxExtractor) {

    }

    public void requestMNMovementExtension(int extension) {

    }

    public Sack getSack() {
        return null;
    }

    public void moveStudent(PieceColor color, StudentMoveSource source, StudentMoveDestination destination) {

    }
}
