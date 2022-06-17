package it.polimi.ingsw.client.view.gui.scene;

import it.polimi.ingsw.client.view.gui.events.GameEndedEvent;
import it.polimi.ingsw.client.view.gui.events.ShowErrorEvent;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.List;

/**
 * FXML injected class that defines the behaviour of the various elements of the lobby phase screen.
 */
public class LobbyPhaseSceneController {
    @FXML
    private StackPane rootPane;
    @FXML
    private HBox usernameList;
    @FXML
    private StackPane modalPane;
    @FXML
    private BorderPane endGamePane;
    @FXML
    private Label endGameText;
    @FXML
    private BorderPane errorPane;
    @FXML
    private Label errorText;
    @FXML
    private StackPane waitingResponsePane;

    /**
     * The callback for when the leave button is clicked
     */
    private final Runnable onLeave;
    /**
     * A {@link ListProperty<String>} representing the player list
     */
    private final ListProperty<String> playerList;
    /**
     * A binding indicating whether the server has any pending messages
     */
    private final ReadOnlyBooleanProperty hasPendingMessages;
    /**
     * The callback ran after the "to main menu" has been pressed on the end game dialog
     */
    private final Runnable afterEndGame;

    /**
     * The ChangeHandler for when the username list changes
     */
    private final ListChangeListener<String> usernameListChangeHandler = change ->
            Platform.runLater(() -> showUsernameList(change.getList()));
    /**
     * The change handler for when we are awaiting a server response
     */
    private final ChangeListener<Boolean> pendingMessagesListener = (obs, oldVal, newVal) ->
            Platform.runLater(() -> waitingResponsePane.setVisible(newVal));

    /**
     * Creates a new instance using the specified callback and property
     *
     * @param playerList         a {@link ListProperty<String>} representing the player list
     * @param hasPendingMessages a binding indicating whether the server has any pending messages
     * @param onLeave            the callback for when the leave button is clicked
     * @param afterEndGame       the callback ran after the "to main menu" has been pressed on the end game dialog
     * @throws IllegalArgumentException if any parameter is null
     */
    public LobbyPhaseSceneController(ListProperty<String> playerList,
                                     ReadOnlyBooleanProperty hasPendingMessages,
                                     Runnable onLeave,
                                     Runnable afterEndGame) {
        if (onLeave == null) throw new IllegalArgumentException("onLeave shouldn't be null");
        if (hasPendingMessages == null) throw new IllegalArgumentException("hasPendingMessages shouldn't be null");
        if (playerList == null) throw new IllegalArgumentException("playerList shouldn't be null");
        if (afterEndGame == null) throw new IllegalArgumentException("afterEndGame shouldn't be null");

        this.onLeave = onLeave;
        this.hasPendingMessages = hasPendingMessages;
        this.playerList = playerList;
        this.afterEndGame = afterEndGame;
    }

    /**
     * Called by FXMLLoader
     */
    public void initialize() {
        WeakListChangeListener<String> listChangeListener = new WeakListChangeListener<>(usernameListChangeHandler);
        playerList.addListener(listChangeListener);
        showUsernameList(playerList.get());

        rootPane.addEventFilter(ShowErrorEvent.ERROR, e -> {
            endGamePane.setVisible(false);
            errorPane.setVisible(true);
            errorText.setText(e.getErrorText());
            modalPane.setVisible(true);
        });
        rootPane.addEventFilter(GameEndedEvent.END, e -> {
            endGamePane.setVisible(true);
            errorPane.setVisible(false);
            endGameText.setText(e.getEndGameText());
            modalPane.setVisible(true);
        });
        hasPendingMessages.addListener(new WeakChangeListener<>(pendingMessagesListener));
    }

    /**
     * Helper method that displays all the elements inside the given observable list
     *
     * @param usernames the {@link ObservableList<String>} to display
     */
    private void showUsernameList(ObservableList<? extends String> usernames) {
        usernameList.getChildren().clear();
        usernames.forEach(p -> usernameList.getChildren().add(buildUsernameLabel(p)));
    }

    /**
     * Convenience method that dynamically builds a {@link Label} displaying the given username
     *
     * @param username the username
     * @return the new label
     */
    private Label buildUsernameLabel(String username) {
        Label l = new Label(username);
        l.getStyleClass().addAll(List.of("ery-label", "slightly-bigger"));
        return l;
    }

    @FXML
    private void onLeaveButtonClicked(MouseEvent ignored) {
        onLeave.run();
    }

    @FXML
    private void onEndGameCloseClicked(MouseEvent ignored) {
        afterEndGame.run();
    }

    @FXML
    private void onErrorCloseClicked(MouseEvent ignored) {
        modalPane.setVisible(false);
    }
}
