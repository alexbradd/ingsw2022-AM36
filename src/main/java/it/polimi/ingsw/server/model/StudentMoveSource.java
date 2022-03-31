package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;

public interface StudentMoveSource {

    public Student sendStudent(PieceColor color);

    public boolean requiresProfessorAssignment();

}
