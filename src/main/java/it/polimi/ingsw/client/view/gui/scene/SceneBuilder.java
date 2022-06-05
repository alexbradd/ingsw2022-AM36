package it.polimi.ingsw.client.view.gui.scene;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Simple interface for classes that build a new {@link Scene} object.
 */
public interface SceneBuilder {
    /**
     * Returns a non-null string that identifies {@link Scene} instances built using this SceneBuilder.
     *
     * @return a string
     */
    String getName();

    /**
     * Build a new {@link Scene}.
     *
     * @return a new {@link Scene}
     * @throws IOException if any IO errors occurred while loading the scene
     */
    Pane build() throws IOException;
}
