package it.polimi.ingsw.client.view.gui.scene;

import it.polimi.ingsw.enums.Mage;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.function.Consumer;

/**
 * A {@link SceneBuilder} that constructs the "choose a mage" scene
 */
public class PreparePhaseSceneBuilder implements SceneBuilder {
    /**
     * A binding containing all the pickable mages
     */
    private final ListBinding<Mage> availableMages;
    /**
     * A binding indicating whether the server has any pending messages
     */
    private final ReadOnlyBooleanProperty hasPendingMessages;
    /**
     * A map associating each mage with its graphic
     */
    private final EnumMap<Mage, Image> mageImages;
    /**
     * A callback for when the mage has been selected by the user
     */
    private final Consumer<String> onChooseMage;
    /**
     * A callback for when the user clicks "to main menu" after a game has ended
     */
    private final Runnable afterEnd;

    /**
     * Creates a new instance with the given resources
     *
     * @param availableMages     a binding containing all the pickable mages
     * @param hasPendingMessages a binding indicating whether the server has any pending messages
     * @param onChooseMage       a callback for when the mage has been selected by the user
     * @param afterEnd           a callback for when the user clicks "to main menu" after a game has ended
     * @throws IllegalArgumentException if any parameter is null
     */
    public PreparePhaseSceneBuilder(ListBinding<Mage> availableMages,
                                    ReadOnlyBooleanProperty hasPendingMessages,
                                    Consumer<String> onChooseMage,
                                    Runnable afterEnd) {
        if (availableMages == null) throw new IllegalArgumentException("availableMages shouldn't be null");
        if (hasPendingMessages == null) throw new IllegalArgumentException("hasPendingMessages shouldn't be null");
        if (onChooseMage == null) throw new IllegalArgumentException("onChooseMage shouldn't be null");
        if (afterEnd == null) throw new IllegalArgumentException("afterEnd shouldn't be null");

        this.availableMages = availableMages;
        this.hasPendingMessages = hasPendingMessages;
        this.onChooseMage = onChooseMage;
        this.afterEnd = afterEnd;
        this.mageImages = new EnumMap<>(Mage.class);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "preparePhase";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pane build() throws IOException {
        for (Mage m : Mage.values()) {
            InputStream in = getClass().getResourceAsStream("/img/mages/" + m.toString().toLowerCase() + ".png");
            assert in != null;
            Image img = new Image(in);
            mageImages.put(m, img);
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/prepare-phase.fxml"));
        loader.setControllerFactory(__ ->
                new PreparePhaseSceneController(availableMages, hasPendingMessages, mageImages, onChooseMage, afterEnd));
        return loader.<StackPane>load();
    }
}
