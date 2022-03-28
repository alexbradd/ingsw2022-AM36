package it.polimi.ingsw.server.model;

public interface StudentMoveDestination {

    public void receiveStudent(Student student);

    public boolean requiresProfessorAssignment();

}
