package it.polimi.ingsw.client.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.control.state.EndState;
import it.polimi.ingsw.client.control.state.GameState;
import it.polimi.ingsw.client.control.state.Lobby;
import it.polimi.ingsw.client.control.state.State;
import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.functional.Tuple;
import javafx.beans.property.ReadOnlyBooleanProperty;

/**
 * Convenience class that bridges the {@link Controller}'s raw interface with one more usable by the {@link GUI}.
 */
public class GUIControllerBridge {
    /**
     * The {@link Controller} instance
     */
    private final Controller controller;

    /**
     * Creates a new bridge using the given {@link Controller} instance
     *
     * @param controller the instance to use
     * @throws IllegalArgumentException if {@code controller} is null
     */
    public GUIControllerBridge(Controller controller) {
        if (controller == null) throw new IllegalArgumentException("controller shouldn't be null");
        this.controller = controller;
    }

    /**
     * Tells the controller to fetch the lobby list
     */
    public void sendFetch() {
        controller.manageUserMessage(buildFetchMessage());
    }

    /**
     * Tells the controller to create a new lobby with the given parameters and join it with the specified username
     *
     * @param username  the username
     * @param lobbyInfo a {@link Tuple} containing the number of player and the expert mode flag
     */
    public void sendCreate(String username, Tuple<Integer, Boolean> lobbyInfo) {
        State state = controller.getState();
        state.updateGameInfo(lobbyInfo.getFirst());
        state.updateGameInfo(lobbyInfo.getSecond());
        state.updateGameInfo(username);
        controller.manageUserMessage(buildCreateMessage(username, lobbyInfo));
    }

    /**
     * Tells the controller to join the given lobby with the specified username
     *
     * @param username the username
     * @param lobby    the lobby
     */
    public void sendJoin(String username, Lobby lobby) {
        State state = controller.getState();
        state.updateGameInfo(lobby.getNumPlayers());
        state.updateGameInfo(lobby.isExpert());
        state.updateGameInfo(username);
        controller.manageUserMessage(buildJoinMessage(username, lobby));
    }

    /**
     * Returns the last error text received.
     *
     * @return the last error text received.
     */
    public String getLastErrorText() {
        return controller.getState().getErrorState().getReason();
    }

    /**
     * Tells the controller to remove this player from the currently playing lobby
     */
    public void sendLeave() {
        String myUsername = controller.getState().getGameInfo().getUsername();
        long id = controller.getState().getGameInfo().getId();
        controller.manageUserMessage(buildLeaveMessage(id, myUsername));
    }

    /**
     * Tell the controller to go back to the main menu
     */
    public void toMainMenu() {
        controller.toMainMenu();
    }

    /**
     * Returns the current game's state.
     *
     * @return the current game's state.
     */
    public GameState getGameState() {
        return controller.getState().getGameState();
    }

    /**
     * Returns the reason why the current game has ended
     *
     * @return the reason why the current game has ended
     * @throws IllegalStateException if the method is called when there is not an {@link EndState} available
     */
    public String getGameEndReason() {
        if (controller.getState().getEndState() == null) throw new IllegalStateException("No end state available");
        return controller.getState().getEndState().getReason();
    }

    /**
     * Returns a ReadOnlyBooleanProperty indicating whether there are any unprocessed messages pending
     *
     * @return a ReadOnlyBooleanProperty
     */
    public ReadOnlyBooleanProperty hasPendingUserMessagesProperty() {
        return controller.hasPendingUserMessagesProperty();
    }

    /**
     * Creates a FETCH message
     *
     * @return a {@link JsonObject} representing the FETCH message
     */
    private static JsonObject buildFetchMessage() {
        return new GUIMessageBuilder("FETCH").build();
    }

    /**
     * Creates a JOIN message for the given username and lobby
     *
     * @param username the username
     * @param lobby    the lobby
     * @return a {@link JsonObject} representing a JOIN message
     * @throws IllegalArgumentException if any parameter is null
     */
    private static JsonObject buildJoinMessage(String username, Lobby lobby) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");
        if (lobby == null) throw new IllegalArgumentException("lobby shouldn't be null");

        return new GUIMessageBuilder("JOIN")
                .addGameId(lobby.getId())
                .addUsername(username)
                .build();
    }

    /**
     * Creates a new CREATE message for the given username and type of game.
     *
     * @param username  the username
     * @param lobbyInfo a {@link Tuple} containing the number of player and the expert mode flag
     * @return a {@link JsonObject} representing the JOIN message
     * @throws IllegalArgumentException if any parameter is null
     */
    private static JsonObject buildCreateMessage(String username, Tuple<Integer, Boolean> lobbyInfo) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");
        if (lobbyInfo == null) throw new IllegalArgumentException("lobbyInfo shouldn't be null");

        JsonArray args = new JsonArray(1);
        JsonObject lobbyInfoJSON = new JsonObject();
        lobbyInfoJSON.addProperty("nPlayers", lobbyInfo.getFirst());
        lobbyInfoJSON.addProperty("expert", lobbyInfo.getSecond());
        args.add(lobbyInfoJSON);
        return new GUIMessageBuilder("CREATE")
                .addUsername(username)
                .addElement("arguments", args)
                .build();
    }

    /**
     * Creates a new LEAVE message for the given username game.
     *
     * @param username the username
     * @param id       the id of the game to leave
     * @return a {@link JsonObject} representing the LEAVE message
     * @throws IllegalArgumentException if any parameter is null
     */
    private static JsonObject buildLeaveMessage(long id, String username) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");

        return new GUIMessageBuilder("LEAVE")
                .addGameId(id)
                .addUsername(username)
                .build();
    }
}
