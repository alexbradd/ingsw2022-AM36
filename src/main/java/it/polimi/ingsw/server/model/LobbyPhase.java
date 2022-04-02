package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;
import java.util.*;

/**
 * It represents the state of the game in which the server is waiting for other players to join. In this phase, the connection
 * and peaceful disconnection of different clients is allowed. It takes track of the number of connected players and, once
 * there is a sufficient amount, it takes the game to the next phase: the {@link PreparePhase}.
 *
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
        while (nPlayersInLobby < game.getnPlayers()) {
            try {
                game.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new PreparePhase(game);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addPlayer(String username) throws OperationNotSupportedException, NullPointerException, PlayerAlreadyInGameException, GameIsFullException {

        if (username == null)
            throw new NullPointerException("username must not be null");
        synchronized (game) {
            if (nPlayersInLobby >= game.getnPlayers())
                throw new GameIsFullException();    // TODO is it needed?

            if (game.getPlayers().contains(username))
                throw new PlayerAlreadyInGameException();

            Player joiningPlayer = new Player(username, game.getnStudentsEntrance(), game.getnTowers(), pickTowerColor());
            game.getPlayers().add(joiningPlayer);
            nPlayersInLobby++;
        }
        game.notifyAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removePlayer(String username) throws OperationNotSupportedException, NullPointerException, PlayerNotInGameException {

        if (username == null)
            throw new NullPointerException("username must not be null");
        synchronized (game) {
            try {
                game.getPlayers().remove(username);
            } catch (IllegalArgumentException e) {
                throw new PlayerNotInGameException();
            }
            nPlayersInLobby--;
        }
        game.notifyAll();
    }

    /**
     * A method that returns a random {@link TowerColor} that hasn't been chosen by any player yet.
     * @return the chosen {@link TowerColor}
     */
    private TowerColor pickTowerColor() {
        List<TowerColor> unchosenColors = Arrays.asList(TowerColor.values());

        if (game.getnPlayers() == 2)
            unchosenColors.remove(TowerColor.GRAY);

        for (PlayerListIterator iter = new PlayerListIterator(0); iter.hasNext(); ) {
            Player p = iter.next();
            pickedColors.remove(p.getTowerColor());
        }

        Collections.shuffle(unchosenColors);
        return unchosenColors.get(0);

    }
}
