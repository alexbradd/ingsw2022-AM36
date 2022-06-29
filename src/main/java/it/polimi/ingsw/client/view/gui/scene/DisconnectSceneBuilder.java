package it.polimi.ingsw.client.view.gui.scene;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * {@link SceneBuilder} for the "connection error" scene.
 */
public class DisconnectSceneBuilder implements SceneBuilder {
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "disconnect";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pane build() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/disconnect.fxml"));
        return loader.load();
    }
}
