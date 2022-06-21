package it.polimi.ingsw.client.view.gui.scene;

import it.polimi.ingsw.client.control.GUIControllerBridge;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * {@link SceneBuilder} for constructing the main game scene
 */
public class GameSceneBuilder implements SceneBuilder {
    /**
     * A callback executed after the "END" dialog has been closed
     */
    private final Runnable afterEnd;
    /**
     * The {@link GUIControllerBridge}
     */
    private final GUIControllerBridge bridge;

    /**
     * Creates a new instance with the given parameters
     *
     * @param afterEnd a callback executed after the "END" dialog has been closed
     * @param bridge   the {@link GUIControllerBridge} to use
     * @throws IllegalArgumentException if any parameter is null
     */
    public GameSceneBuilder(Runnable afterEnd, GUIControllerBridge bridge) {
        if (afterEnd == null) throw new IllegalArgumentException("afterEnd shouldn't be null");
        if (bridge == null) throw new IllegalArgumentException("bridge shouldn't be null");

        this.afterEnd = afterEnd;
        this.bridge = bridge;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "game";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pane build() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/game.fxml"));
        loader.setControllerFactory(__ -> new GameSceneController(afterEnd, bridge));
        return loader.load();
    }
}
