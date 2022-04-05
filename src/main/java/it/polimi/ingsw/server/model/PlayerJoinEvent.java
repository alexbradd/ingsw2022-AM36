package it.polimi.ingsw.server.model;
import it.polimi.ingsw.server.model.exceptions.PlayerAlreadyInGameException;

import javax.naming.OperationNotSupportedException;

public class PlayerJoinEvent implements UserEvent {
    private final String username;

    public PlayerJoinEvent(String username) {
        this.username = username;
    }
    @Override
    public void consume(Phase p) throws OperationNotSupportedException, PlayerAlreadyInGameException {
        p.addPlayer(username);
    }
}
