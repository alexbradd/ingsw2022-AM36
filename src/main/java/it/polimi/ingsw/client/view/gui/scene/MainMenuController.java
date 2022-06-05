package it.polimi.ingsw.client.view.gui.scene;

import it.polimi.ingsw.client.control.state.Lobby;
import it.polimi.ingsw.client.view.gui.events.RefreshLobbiesEvent;
import it.polimi.ingsw.client.view.gui.events.ShowErrorEvent;
import it.polimi.ingsw.functional.Tuple;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.function.BiConsumer;

/**
 * FXML injected class that defines the behaviour of the various elements of the main menu scene.
 */
public class MainMenuController {
    @FXML
    private StackPane rootPane;
    @FXML
    private TextField usernameField;
    @FXML
    private VBox lobbyList;
    @FXML
    private ChoiceBox<Integer> nPlayersChoiceBox;
    @FXML
    private ToggleButton expertModeToggle;
    @FXML
    private StackPane errorPane;
    @FXML
    private Label errorText;

    /**
     * The callback for when the refresh button is clicked.
     */
    private final Runnable onRefresh;
    /**
     * The callback for when the create button is clicked.
     */
    private final BiConsumer<String, Tuple<Integer, Boolean>> onCreate;
    /**
     * The callback for when the join button is clicked
     */
    private final BiConsumer<String, Lobby> onJoin;

    /**
     * Creates a new instance using the given callbacks.
     *
     * @param onRefresh the callback for when the refresh button is clicked
     * @param onCreate  the callback for when the create button is clicked
     * @param onJoin    the callback for when the join button is clicked
     * @throws IllegalArgumentException if any parameter is null
     */
    public MainMenuController(Runnable onRefresh, BiConsumer<String, Tuple<Integer, Boolean>> onCreate, BiConsumer<String, Lobby> onJoin) {
        if (onRefresh == null) throw new IllegalArgumentException("onRefresh shouldn't be null");
        if (onCreate == null) throw new IllegalArgumentException("onCreate shouldn't be null");
        if (onJoin == null) throw new IllegalArgumentException("onJoin shouldn't be null");

        this.onRefresh = onRefresh;
        this.onCreate = onCreate;
        this.onJoin = onJoin;
    }

    /**
     * Called by FXMLLoader
     */
    public void initialize() {
        nPlayersChoiceBox.getSelectionModel().selectFirst();
        rootPane.addEventFilter(RefreshLobbiesEvent.REFESH, (e) -> {
            clearLobbyList();
            e.getLobbies().forEach(l -> {
                Node n = buildJoinGameGroup(l, onJoin);
                addLobby(n);
            });
        });
        rootPane.addEventFilter(ShowErrorEvent.ERROR, (e) -> {
            errorText.setText(e.getErrorText());
            errorPane.setVisible(true);
        });
    }

    /**
     * Helper method that creates the group containing the info of a join-able lobby and its "JOIN" button
     *
     * @param lobby        the {@link Lobby}
     * @param joinCallback function called on lobby join
     * @return a {@link Pane}
     */
    private Pane buildJoinGameGroup(Lobby lobby, BiConsumer<String, Lobby> joinCallback) {
        Label id = new Label("Id: " + lobby.getId()),
                players = new Label(lobby.getNumPlayers() + " players"),
                expert = new Label(lobby.isExpert() ? "Expert mode" : "Simple mode");
        Button joinButton = new Button("Join");

        id.getStyleClass().add("ery-label");
        players.getStyleClass().add("ery-label");
        expert.getStyleClass().add("ery-label");
        joinButton.setOnMouseClicked((e) -> {
            String username = usernameField.getText();
            if (username == null || username.length() == 0)
                showIllegalUsernameError();
            else
                joinCallback.accept(username, lobby);
        });

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(25);
        VBox.setVgrow(grid, Priority.NEVER);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().add(constraints);
        }
        grid.addColumn(0, id);
        grid.addColumn(1, players);
        grid.addColumn(2, expert);
        grid.addColumn(3, joinButton);
        grid.getStyleClass().add("ery-join");
        GridPane.setHalignment(id, HPos.LEFT);
        GridPane.setHalignment(players, HPos.RIGHT);
        GridPane.setHalignment(expert, HPos.CENTER);
        GridPane.setHalignment(joinButton, HPos.CENTER);

        return grid;
    }

    /**
     * Removes all children from the lobby list container
     */
    private void clearLobbyList() {
        lobbyList.getChildren().clear();
    }

    /**
     * Adds the given lobby to the lobby list
     *
     * @param lobby the lobby to add
     * @throws IllegalArgumentException if {@code lobby} is null
     */
    private void addLobby(Node lobby) {
        if (lobby == null) throw new IllegalArgumentException("lobby shouldn't be null");
        lobbyList.getChildren().add(lobby);
    }

    /**
     * Convenience function for firing a {@link ShowErrorEvent} at the scene when the username is invalid
     *
     * @throws IllegalArgumentException if {@code text is null}
     */
    private void showIllegalUsernameError() {
        rootPane.fireEvent(new ShowErrorEvent(ShowErrorEvent.ERROR, "You must specify a username"));
    }

    @FXML
    private void onErrorCloseClick(MouseEvent ignored) {
        errorPane.setVisible(false);
    }

    @FXML
    private void onRefreshButtonClicked(MouseEvent ignored) {
        onRefresh.run();
    }

    @FXML
    private void onCreateButtonClicked(MouseEvent ignored) {
        String username = usernameField.getText();
        int nPlayer = nPlayersChoiceBox.getValue();
        boolean isExpert = expertModeToggle.isSelected();
        if (username == null || username.length() == 0)
            showIllegalUsernameError();
        else
            onCreate.accept(username, new Tuple<>(nPlayer, isExpert));
    }
}
