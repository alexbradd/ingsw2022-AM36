package it.polimi.ingsw.server.model;

public interface UserEvent {
    public void consume(Phase p);
}
