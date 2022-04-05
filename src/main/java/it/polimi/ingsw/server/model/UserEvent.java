package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.exceptions.PlayerAlreadyInGameException;
import it.polimi.ingsw.server.model.exceptions.PlayerNotInGameException;

import javax.naming.OperationNotSupportedException;

public interface UserEvent {
    public void consume(Phase p) throws OperationNotSupportedException, PlayerAlreadyInGameException, PlayerNotInGameException;
}
