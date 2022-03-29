package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;

public interface UserEvent {
    public void consume(Phase p) throws OperationNotSupportedException, PlayerAlreadyInGameException, PlayerNotInGameException;
}
