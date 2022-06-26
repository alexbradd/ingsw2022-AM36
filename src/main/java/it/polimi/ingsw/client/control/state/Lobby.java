package it.polimi.ingsw.client.control.state;

/**
 * Client's representation of a lobby information.
 *
 * @author Mattia Busso
 */
public class Lobby {

    /**
     * The id of the lobby.
     */
    private long id;

    /**
     * The game's number of players.
     */
    private int nPlayers;

    /**
     * If the game is on expert mode or not.
     */
    private boolean expert;

    // getters

    /**
     * Returns the lobby's id.
     *
     * @return the lobby's id
     */
    public long getId() {
        return id;
    }

    /**
     * Flag that indicates if the lobby is of a game in the rejoining state.
     */
    private boolean rejoining;

    /**
     * Number of players inside the lobby.
     */
    private int playersConnected;

    /**
     * Returns the number of players of the game.
     *
     * @return the number of players of the game
     */
    public int getNumPlayers() {
        return nPlayers;
    }

    /**
     * Returns the expert mode of the game
     * @return expert
     */
    public boolean isExpert() {
        return expert;
    }

    /**
     * Returns the rejoining flag.
     * @return the rejoining flag
     */
    public boolean isRejoining() {
        return rejoining;
    }

    /**
     * Returns the number of players inside the lobby.
     * @return the number of players in the lobby
     */
    public int getConnectedPlayers() {
        return playersConnected;
    }

    // stringify

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "LOBBY N#" + id + "\n" +
                "* " + nPlayers + " players game" + "\n" +
                "* " + (expert ? "expert" : "not expert");
    }
}
