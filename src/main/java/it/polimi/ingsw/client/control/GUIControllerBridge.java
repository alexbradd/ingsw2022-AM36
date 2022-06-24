package it.polimi.ingsw.client.control;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.client.control.state.*;
import it.polimi.ingsw.client.view.gui.GUI;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.functional.Tuple;
import javafx.beans.property.ReadOnlyBooleanProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        String myUsername = getMyUsername();
        long id = getGameId();
        controller.manageUserMessage(buildLeaveMessage(id, myUsername));
    }

    /**
     * Tell the controller that this player has chosen the given mage
     *
     * @param mage the chosen mage
     */
    public void sendChooseMage(String mage) {
        String myUsername = getMyUsername();
        long id = getGameId();
        controller.manageUserMessage(buildChooseMageMessage(id, myUsername, mage));
    }

    /**
     * Tell the controller that this player has chosen the given assistant
     *
     * @param assistant the chosen assistant
     */
    public void sendPlayAssistant(String assistant) {
        String myUsername = getMyUsername();
        long id = getGameId();
        controller.manageUserMessage(buildPlayAssistantMessage(id, myUsername, assistant));
    }

    /**
     * Tell the controller that this player has chosen the student to move to the given island
     *
     * @param color  the chosen color
     * @param island the island to move to
     */
    public void sendMoveStudent(String color, int island) {
        String myUsername = getMyUsername();
        long id = getGameId();
        controller.manageUserMessage(buildMoveStudentMessage(id, myUsername, color, island));
    }

    /**
     * Tell the controller that this player has chosen the student to move to its hall
     *
     * @param color the chosen color
     */
    public void sendMoveStudent(String color) {
        String myUsername = getMyUsername();
        long id = getGameId();
        controller.manageUserMessage(buildMoveStudentMessage(id, myUsername, color));
    }

    /**
     * Tell the controller that this player wants to move mother nature by the given amount of steps
     *
     * @param steps the number of steps
     */
    public void sendMoveMn(int steps) {
        String myUsername = controller.getState().getGameInfo().getUsername();
        long id = controller.getState().getGameInfo().getId();
        controller.manageUserMessage(buildMoveMnMessage(id, myUsername, steps));
    }

    /**
     * Tell the controller this current player has chosen their cloud.
     *
     * @param cloudId the clouds id
     */
    public void sendPickCloud(int cloudId) {
        String myUsername = controller.getState().getGameInfo().getUsername();
        long id = controller.getState().getGameInfo().getId();
        controller.manageUserMessage(buildCloudPickMessage(id, myUsername, cloudId));
    }

    /**
     * Tells the controller that this player has invoked the given CharacterType with the given steps
     *
     * @param type  the CharacterType
     * @param steps the steps for the character
     */
    public void sendPlayCharacter(CharacterType type, List<Map<String, String>> steps) {
        String username = getMyUsername();
        long id = getGameId();
        controller.manageUserMessage(buildPlayCharacterMessage(id, username, type, steps));
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
     * Returns the current game's id
     *
     * @return the current game's id
     * @throws IllegalStateException if the method is called when there is not an {@link GameInfo} available
     */
    public long getGameId() {
        if (controller.getState().getGameInfo() == null) throw new IllegalStateException("No game info available");
        return controller.getState().getGameInfo().getId();
    }

    /**
     * Returns this player's username
     *
     * @return this player's username
     * @throws IllegalStateException if the method is called when there is not an {@link GameInfo} available
     */
    public String getMyUsername() {
        if (controller.getState().getGameInfo() == null) throw new IllegalStateException("No game info available");
        return controller.getState().getGameInfo().getUsername();
    }

    /**
     * Returns whether this game is in expert mode or not.
     *
     * @return whether this game is in expert mode or not.
     * @throws IllegalStateException if the method is called when there is not an {@link EndState} available
     */
    public boolean isExpertMode() {
        if (controller.getState().getGameInfo() == null) throw new IllegalStateException("No game info available");
        return controller.getState().getGameInfo().isExpert();
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
     * Returns the list of winners.
     *
     * @return the list of winners
     * @throws IllegalStateException if the method is called when there is not an {@link EndState} available
     */
    public List<String> getWinners() {
        if (controller.getState().getEndState() == null) throw new IllegalStateException("No end state available");
        return Arrays.asList(controller.getState().getEndState().getWinners());
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
     * @return a {@link JsonObject} representing the CREATE message
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

    /**
     * Creates a new CHOOSE_MAGE message for the given username, game and mage.
     *
     * @param username the username
     * @param id       the id of the game
     * @param mage     the name of the chosen mage
     * @return a {@link JsonObject} representing the CHOOSE_MAGE message
     * @throws IllegalArgumentException if any parameter is null
     */
    private static JsonObject buildChooseMageMessage(long id, String username, String mage) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");
        if (mage == null) throw new IllegalArgumentException("mage shouldn't be null");

        JsonArray args = new JsonArray(1);
        args.add(mage);
        return new GUIMessageBuilder("CHOOSE_MAGE")
                .addGameId(id)
                .addUsername(username)
                .addElement("arguments", args)
                .build();
    }

    /**
     * Creates a new PLAY_ASSISTANTS message for the given username, game and assistant.
     *
     * @param username  the username
     * @param id        the id of the game
     * @param assistant the name of the chosen assistant
     * @return a {@link JsonObject} representing the PLAY_ASSISTANTS message
     * @throws IllegalArgumentException if any parameter is null
     */
    private static JsonObject buildPlayAssistantMessage(long id, String username, String assistant) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");
        if (assistant == null) throw new IllegalArgumentException("assistant shouldn't be null");
        JsonArray args = new JsonArray(1);
        args.add(assistant);
        return new GUIMessageBuilder("PLAY_ASSISTANTS")
                .addGameId(id)
                .addUsername(username)
                .addElement("arguments", args)
                .build();
    }

    /**
     * Creates a new MOVE_STUDENT with ISLAND destination message for the given username, game and color.
     *
     * @param username the username
     * @param id       the id of the game
     * @param color    the name of the chosen color
     * @param island   the island index
     * @return a {@link JsonObject} representing the PLAY_ASSISTANTS message
     * @throws IllegalArgumentException if any parameter is null
     */
    private static JsonObject buildMoveStudentMessage(long id, String username, String color, int island) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");
        if (color == null) throw new IllegalArgumentException("color shouldn't be null");

        JsonArray args = new JsonArray(1);
        JsonObject move = new JsonObject();
        move.addProperty("color", color);
        move.addProperty("destination", "ISLAND");
        move.addProperty("index", island);
        args.add(move);

        return new GUIMessageBuilder("MOVE_STUDENT")
                .addGameId(id)
                .addUsername(username)
                .addElement("arguments", args)
                .build();
    }

    /**
     * Creates a new MOVE_STUDENT with HALL destination message for the given username, game and color.
     *
     * @param username the username
     * @param id       the id of the game
     * @param color    the name of the chosen color
     * @return a {@link JsonObject} representing the PLAY_ASSISTANTS message
     * @throws IllegalArgumentException if any of parameter is null
     */
    private static JsonObject buildMoveStudentMessage(long id, String username, String color) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");
        if (color == null) throw new IllegalArgumentException("color shouldn't be null");

        JsonArray args = new JsonArray(1);
        JsonObject move = new JsonObject();
        move.addProperty("color", color);
        move.addProperty("destination", "HALL");
        args.add(move);

        return new GUIMessageBuilder("MOVE_STUDENT")
                .addGameId(id)
                .addUsername(username)
                .addElement("arguments", args)
                .build();
    }

    /**
     * Creates a new MOVE_MN message for the given username and game.
     *
     * @param username the username
     * @param id       the id of the game
     * @param steps    the amount of steps to move mother nature
     * @return a {@link JsonObject} representing the MOVE_MN message
     * @throws IllegalArgumentException if any parameter is null
     */
    private static JsonObject buildMoveMnMessage(long id, String username, int steps) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");
        JsonArray arguments = new JsonArray();
        arguments.add(steps);
        return new GUIMessageBuilder("MOVE_MN")
                .addGameId(id)
                .addUsername(username)
                .addElement("arguments", arguments)
                .build();
    }

    /**
     * Creates a new PICK_CLOUD message for the given username and game.
     *
     * @param username the username
     * @param id       the id of the game
     * @param cloudId  the chosen cloud's id
     * @return a {@link JsonObject} representing the PICK_CLOUD message
     * @throws IllegalArgumentException if any parameter is null
     */
    private static JsonObject buildCloudPickMessage(long id, String username, int cloudId) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");

        JsonArray args = new JsonArray(1);
        args.add(cloudId);
        return new GUIMessageBuilder("PICK_CLOUD")
                .addGameId(id)
                .addUsername(username)
                .addElement("arguments", args)
                .build();
    }

    /**
     * Creates a new PLAY_CHARACTER message for the given username, game and character.
     *
     * @param username the username
     * @param id       the id of the game
     * @param type     the character type
     * @param steps    a list of invocation steps. Each step is a String-String map containing the invocation parameters
     * @return a {@link JsonObject} representing the PLAY_CHARACTER message
     * @throws IllegalArgumentException if any parameter is null
     */
    private static JsonObject buildPlayCharacterMessage(long id,
                                                        String username,
                                                        CharacterType type,
                                                        List<Map<String, String>> steps) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");
        if (type == null) throw new IllegalArgumentException("type shouldn't be null");
        if (steps == null) throw new IllegalArgumentException("steps shouldn't be null");

        JsonObject invocation = new JsonObject();
        invocation.addProperty("character", type.toString());
        if (!steps.isEmpty()) {
            JsonArray stepArray = new JsonArray(steps.size());
            steps.forEach(m -> {
                JsonObject step = new JsonObject();
                m.forEach(step::addProperty);
                stepArray.add(step);
            });
            invocation.add("steps", stepArray);
        }

        JsonArray args = new JsonArray(1);
        args.add(invocation);
        return new GUIMessageBuilder("PLAY_CHARACTER")
                .addGameId(id)
                .addUsername(username)
                .addElement("arguments", args)
                .build();
    }
}
