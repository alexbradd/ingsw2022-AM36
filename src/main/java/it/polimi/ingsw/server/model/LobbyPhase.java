package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;

/**
 * It represents the state of the game in which the server is waiting for other players to join. In this phase, the connection
 * and peaceful disconnection of different clients is allowed. It takes track of the number of connected players and, once
 * there is a sufficient amount, it takes the game to the next phase: the {@link PreparePhase}.
 * @author Leonardo Bianconi
 * @see Phase
 * @see PreparePhase
 */

public class LobbyPhase extends Phase {
    /**
     * The number of players currently in the lobby.
     */
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
