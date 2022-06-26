package it.polimi.ingsw.client.control.state;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Objects;

/**
 * This class encapsulates all the possible states of the game.
 *
 * @author Mattia Busso
 */
public class State {

    /**
     * The game's info
     */
    private GameInfo gameInfo;

    /**
     * The game's state
     */
    private GameState gameState;

    /**
     * The end state of the game
     */
    private EndState endState;

    /**
     * The error state of the game
     */
    private ErrorState errorState;

    /**
     * The game's fetched lobbies
     */
    private Lobby[] lobbies;

    // updates

    /**
     * Updates the game's information.
     *
     * @param numPlayers the number of players of the game
     */
    public void updateGameInfo(int numPlayers) {
        if (gameInfo == null) {
            gameInfo = new GameInfo();
        }
        gameInfo.setNumPlayers(numPlayers);
    }

    /**
     * Updates the game's information.
     *
     * @param id the id of the game
     */
    public void updateGameInfo(long id) {
        if (gameInfo == null) {
            gameInfo = new GameInfo();
        }
        gameInfo.setId(id);
    }

    /**
     * Updates the game's information.
     *
     * @param username the username of the player
     */
    public void updateGameInfo(String username) {
        if (gameInfo == null) {
            gameInfo = new GameInfo();
        }
        gameInfo.setUsername(username);
    }

    /**
     * Updates the game's information.
     *
     * @param isExpert the expert mode
     */
    public void updateGameInfo(boolean isExpert) {
        if (gameInfo == null) {
            gameInfo = new GameInfo();
        }
        gameInfo.setExpertMode(isExpert);
    }

    /**
     * Updates the game's state.
     *
     * @param o the {@code JsonObject} corresponding to an {@code update} message from the server
     *        (it is assumed to be correct)
     */
    public void updateGameState(JsonObject o) {
        if(gameState == null) {
            Gson gson = new Gson();
            gameState = new GameState();
            gameState.update(o.get("update").getAsJsonObject());
            updateGameInfo(gson.fromJson(o.get("id"), long.class));
        }
        else {
            gameState.update(o.get("update").getAsJsonObject());
        }
    }

    /**
     * Updates the game's fetched lobbies.
     *
     * @param o the {@code JsonObject} corresponding to a {@code lobbies} message from the server
     *          (it is assumed to be correct)
     */
    public void updateLobbies(JsonObject o) {
        Gson gson = new Gson();
        lobbies = gson.fromJson(o.get("lobbies"), Lobby[].class);
    }

    /**
     * Updates the game's end state.
     *
     * @param o the {@code JsonObject} corresponding to a {@code end} message from the server
     *        (it is assumed to be correct)
     * @return {@code true} if the application has to be shut down, {@code false} otherwise
     */
    public boolean updateEndState(JsonObject o) {
        Gson gson = new Gson();
        endState = gson.fromJson(o, EndState.class);
        return Objects.equals(endState.getReason(), "server error");
    }

    /**
     * Updates the error state of the game.
     *
     * @param o the {@code JsonObject} corresponding to an {@code error} message from the server
     *        (it is assumed to be correct)
     */
    public void updateErrorState(JsonObject o) {
        Gson gson = new Gson();
        errorState = gson.fromJson(o, ErrorState.class);
    }

    // getters

    /**
     * End state getter.
     *
     * @return the end state
     */
    public EndState getEndState() {
        return endState;
    }

    /**
     * Error state getter.
     *
     * @return the error state.
     */
    public ErrorState getErrorState() {
        return errorState;
    }

    /**
     * Game info getter.
     *
     * @return the game's info
     */
    public GameInfo getGameInfo() {
        return gameInfo;
    }

    /**
     * Game state getter
     *
     * @return the game's state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Lobbies getter.
     *
     * @return the game's lobbies
     */
    public Lobby[] getLobbies() {
        return lobbies;
    }

    // reset

    /**
     * Resets the state.
     */
    public void resetState() {
        gameState = null;
        gameInfo = null;
        lobbies = null;
        endState = null;
        errorState = null;
    }

    // helpers

    /**
     * Checks if a given lobby is present.
     *
     * @param id a lobby id
     * @return {@code true} if a lobby with the given id exists, {@code false} otherwise
     */
    public boolean isValidLobby(int id) {
        boolean isValid = false;
        for(Lobby l : lobbies) {
            if (l.getId() == id) {
                isValid = true;
                break;
            }
        }
        return isValid;
    }

    /**
     * Returns the lobby with the given id.
     *
     * @param id the id of the lobby
     * @return the lobby with the given id
     * @throws IllegalArgumentException if the given id is not valid
     */
    public Lobby getLobby(int id) throws IllegalArgumentException {
        Lobby lobby = null;
        for(Lobby l : lobbies) {
            if (l.getId() == id) {
                lobby = l;
                break;
            }
        }
        if(lobby == null) throw new IllegalArgumentException("invalid lobby id");
        return lobby;
    }

    /**
     * Checks if it's currently the user turn.
     *
     * @return {@code true} if it is the user's turn to play, {@code false} otherwise
     */
    public boolean isPlayerTurn() {
        if(gameState.getCurrentPlayer() == null) return false;
        return Objects.equals(gameState.getCurrentPlayer(), gameInfo.getUsername());
    }

}
