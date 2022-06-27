package it.polimi.ingsw.client.view.gui.scene;

import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

/**
 * A {@link SceneBuilder} that constructs the lobby phase scene
 */
public class LobbyPhaseSceneBuilder implements SceneBuilder {
    /**
     * The callback for when the leave button is clicked
     */
    private final Runnable onLeave;
    /**
     * A binding indicating whether the server has any pending messages
     */
    private final ReadOnlyBooleanProperty hasPendingMessages;
    /**
     * A boolean property indicating whether this lobby is waiting for other players to join it
     */
    private final ReadOnlyBooleanProperty rejoining;
    /*
     * A {@link ListProperty<String>} representing the player list
     */
    private final ListProperty<String> playerList;
    /**
     * The callback ran after the "to main menu" has been pressed on the end game dialog
     */
    private final Runnable afterEndGame;

    /**
     * Creates a new instance using the specified callback and property
     *
     * @param playerList         a {@link ListProperty<String>} representing the player list
     * @param hasPendingMessages a binding indicating whether the server has any pending messages
     * @param rejoining          a boolean property indicating whether this lobby is waiting for other players to join
     *                           it
     * @param onLeave            the callback for when the leave button is clicked
     * @param afterEndGame       the callback ran after the "to main menu" has been pressed on the end game dialog
     * @throws IllegalArgumentException if any parameter is null
     */
    public LobbyPhaseSceneBuilder(ListProperty<String> playerList,
                                  ReadOnlyBooleanProperty hasPendingMessages,
                                  ReadOnlyBooleanProperty rejoining,
                                  Runnable onLeave,
                                  Runnable afterEndGame) {
        if (onLeave == null) throw new IllegalArgumentException("onLeave shouldn't be null");
        if (hasPendingMessages == null) throw new IllegalArgumentException("hasPendingMessages shouldn't be null");
        if (rejoining == null) throw new IllegalArgumentException("rejoining shouldn't be null");
        if (playerList == null) throw new IllegalArgumentException("playerList shouldn't be null");
        if (afterEndGame == null) throw new IllegalArgumentException("afterEndGame shouldn't be null");

        this.onLeave = onLeave;
        this.hasPendingMessages = hasPendingMessages;
        this.rejoining = rejoining;
        this.playerList = playerList;
        this.afterEndGame = afterEndGame;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "lobbyPhase";
    }

    /**
     * Build a new {@link Scene}.
     *
     * @return a new {@link Scene}
     * @throws IOException if any IO errors occurred while loading the scene
     */
    @Override
    public Pane build() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/lobby-phase.fxml"));
        loader.setControllerFactory(__ ->
                new LobbyPhaseSceneController(playerList, hasPendingMessages, rejoining, onLeave, afterEndGame));
        return loader.<StackPane>load();
    }
}
