package it.polimi.ingsw.server.model;
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
