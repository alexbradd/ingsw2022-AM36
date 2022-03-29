package it.polimi.ingsw.server.model;
import javax.naming.OperationNotSupportedException;

public class PlayerLeaveEvent implements UserEvent {
    private final String username;

    public PlayerLeaveEvent(String username) {
        this.username = username;
    }
    @Override
    public void consume(Phase p) throws OperationNotSupportedException, PlayerNotInGameException {
        p.removePlayer(username);
    }
}