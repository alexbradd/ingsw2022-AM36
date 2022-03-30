package it.polimi.ingsw.server.model;

public interface StudentMoveSource {

    public Student sendStudent(PieceColor color);

    public boolean requiresProfessorAssignment();

}
