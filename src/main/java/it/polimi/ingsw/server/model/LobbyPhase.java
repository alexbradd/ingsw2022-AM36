package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;

public class LobbyPhase extends Phase {
    private int nPlayersInLobby;

    /**
     * {@inheritDoc}
     */
    protected LobbyPhase(Game game) {
        super(game);
        nPlayersInLobby = 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Phase doPhase() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlayer(String username) throws OperationNotSupportedException, NullPointerException, PlayerAlreadyInGameException {
        super.addPlayer(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePlayer(String username) throws OperationNotSupportedException, NullPointerException, PlayerNotInGameException {
        super.removePlayer(username);
    }
}
