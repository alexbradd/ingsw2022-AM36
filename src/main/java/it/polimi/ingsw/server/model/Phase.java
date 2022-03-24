package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;

/**
 * The Phase class represents a single state of the game, it is linked to the Game instance and it is a facade facing
 * the controller via the Command pattern, showing the available operations (its methods), that interact with the
 * internal state of all model entities.
 * @author Leonardo Bianconi
 * @see Game
 * @see LobbyPhase
 * @see PreparePhase
 * @see PlanningPhase
 * @see ActionPhase
 * @see EndgamePhase
 * */

abstract public class Phase {
    private Game game;

    /**
     * The default constructor.
     * @param game the {@link Game} instance
     */
    protected Phase(Game game) {
        this.game = game;
    }

    /**
     * The main method of the phase, keeps waiting for the correct function calls, i.e. the ones that make the game evolve to
     * new phases.
     * @return the next phase of the game.
     */
    public Phase doPhase() {}

    /**
     *
     * @param id
     * @return
     */
    public Character getCharacter(int id) {}

    /**
     *
     * @param id
     * @return
     */
    public Island getIsland(int id) {}

    /**
     *
     * @param username
     * @param id
     */
    public void chooseMageDeck(String username, int id) {}

    /**
     *
     * @param username
     * @param id
     */
    public void playAssistant(String username, int id) {}

    /**
     *
     * @param username
     * @return
     */
    public Entrance getPlayerEntrance(String username) {}

    /**
     *
     * @param username
     * @return
     * @throws OperationNotSupportedException
     */
    public Hall getPlayerHall(String username) {
        throw new OperationNotSupportedException();
    }

    public void moveStudent(PieceColor color, StudentMoveSource source, StudentMoveDestination destination) {}
    public void moveMN(String username, int steps) {}
    public void playCharacter(String username, Character character, int... args) {}
    public void pickCloud(String username, int id) {}
    public void addPlayer(String username) {}
    public void removePlayer(String username) {}
}
