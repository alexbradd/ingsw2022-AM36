package it.polimi.ingsw.client.view.gui.scene;

import it.polimi.ingsw.client.control.state.Lobby;
import it.polimi.ingsw.functional.Tuple;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * A {@link SceneBuilder} that constructs the main menu scene.
 */
public class MainMenuSceneBuilder implements SceneBuilder {
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
     * Creates a new instance using the specified callbacks
     *
     * @param onRefresh the callback for when the refresh button is clicked
     * @param onCreate  The callback for when the create button is clicked
     * @param onJoin    the callback for when the join button is clicked
     * @throws IllegalArgumentException if any parameter is null
     */
    public MainMenuSceneBuilder(Runnable onRefresh, BiConsumer<String, Tuple<Integer, Boolean>> onCreate, BiConsumer<String, Lobby> onJoin) {
        if (onRefresh == null) throw new IllegalArgumentException("onRefresh shouldn't be null");
        if (onCreate == null) throw new IllegalArgumentException("onCreate shouldn't be null");
        if (onJoin == null) throw new IllegalArgumentException("onJoin shouldn't be null");

        this.onRefresh = onRefresh;
        this.onCreate = onCreate;
        this.onJoin = onJoin;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "mainMenu";
    }

    /**
     * Reads the main menu scene's FXML from disk, injects the necessary resources and then returns it
     *
     * @return A {@link Scene}
     * @throws IOException if an IO error occurred
     */
    public Pane build() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main-menu.fxml"));
        loader.setControllerFactory(__ -> new MainMenuController(onRefresh, onCreate, onJoin));
        return loader.<StackPane>load();
    }
}
