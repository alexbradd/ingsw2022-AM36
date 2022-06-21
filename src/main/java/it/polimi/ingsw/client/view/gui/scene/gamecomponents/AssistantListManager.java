package it.polimi.ingsw.client.view.gui.scene.gamecomponents;

import it.polimi.ingsw.client.view.gui.scene.ChangeListenerReferenceManager;
import it.polimi.ingsw.enums.AssistantType;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ListExpression;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.function.BiConsumer;

import static it.polimi.ingsw.client.view.gui.AssetManager.getAssistantPngs;
import static it.polimi.ingsw.client.view.gui.GUIUtils.runLaterIfNotOnFxThread;

/**
 * Layout manager for the available assistants list
 */
public class AssistantListManager {
    /**
     * The assistant graphic's height
     */
    private final static int ASSISTANT_GFX_H = 140;
    /**
     * The reference manager
     */
    private final ChangeListenerReferenceManager refManager;
    /**
     * The root pane in which to inject dynamic content
     */
    private final Pane assistantsPane;
    /**
     * Boolean binding indicating whether the layout should be interactive
     */
    private final BooleanBinding shouldBeEnabled;
    /**
     * The list of the assistants available to this player
     */
    private final ListExpression<AssistantType> playerAssistants;
    /**
     * Handler for when an assistant is clicked (fired only when the layout is interactive)
     */
    private final BiConsumer<MouseEvent, AssistantType> onAssistantClick;

    /**
     * Creates a new instance with the given parameters
     *
     * @param assistantsPane   The root pane in which to inject dynamic content
     * @param shouldBeEnabled  Boolean binding indicating whether the layout should be interactive
     * @param playerAssistants The list of the assistants available to this player
     * @param onAssistantClick Handler for when an assistant is clicked
     * @throws IllegalArgumentException if any parameter is null
     */
    public AssistantListManager(Pane assistantsPane,
                                BooleanBinding shouldBeEnabled,
                                ListExpression<AssistantType> playerAssistants,
                                BiConsumer<MouseEvent, AssistantType> onAssistantClick) {
        if (assistantsPane == null) throw new IllegalArgumentException("assistantsPane shouldn't be null");
        if (shouldBeEnabled == null) throw new IllegalArgumentException("shouldBeEnabled shouldn't be null");
        if (playerAssistants == null) throw new IllegalArgumentException("playerAssistants shouldn't be null");
        if (onAssistantClick == null) throw new IllegalArgumentException("onAssistantClick shouldn't be null");

        this.refManager = new ChangeListenerReferenceManager();
        this.assistantsPane = assistantsPane;
        this.shouldBeEnabled = shouldBeEnabled;
        this.playerAssistants = playerAssistants;
        this.onAssistantClick = onAssistantClick;
    }

    /**
     * Start reactively managing the layout.
     * <p>
     * Note: the absence of a stopManaging() here is intentional. The lifecycle of each GameState object is as long as
     * that of its view. Therefore, we have to worry only about internal cleanup between relayouts.
     */
    public void startManaging() {
        playerAssistants.addListener((ListChangeListener<AssistantType>) change ->
                runLaterIfNotOnFxThread(() -> layout(change.getList())));
        shouldBeEnabled.addListener((_1, _2, v) -> enableIfTrue(assistantsPane, v));

        enableIfTrue(assistantsPane, shouldBeEnabled.get());
        layout(playerAssistants.get());
    }

    /**
     * Cleans the slate and injects up-to-date content into the root pane.
     *
     * @param list the assistants list
     */
    private void layout(ObservableList<? extends AssistantType> list) {
        refManager.unregisterAll();
        assistantsPane.getChildren().clear();
        for (AssistantType t : list) {
            ImageView image = new ImageView(getAssistantPngs().get(t));
            image.setPreserveRatio(true);
            image.setFitHeight(ASSISTANT_GFX_H);
            image.setOnMouseClicked(e -> onAssistantClick.accept(e, t));
            refManager.registerListener(shouldBeEnabled, (_1, _2, v) -> enablePointerOnImageViewIfTrue(image, v));
            enablePointerOnImageViewIfTrue(image, shouldBeEnabled.get());
            assistantsPane.getChildren().add(image);
        }
    }

    /**
     * Shows a pointer on the given image if the value is true
     *
     * @param image             the image
     * @param shouldHavePointer the value
     */
    private void enablePointerOnImageViewIfTrue(ImageView image, boolean shouldHavePointer) {
        runLaterIfNotOnFxThread(() -> {
            if (shouldHavePointer) image.setCursor(Cursor.HAND);
            else image.setCursor(Cursor.DEFAULT);
        });
    }

    /**
     * Enables and highlights a node if the value is true
     *
     * @param node    the node
     * @param enabled the value
     */
    private void enableIfTrue(Node node, boolean enabled) {
        runLaterIfNotOnFxThread(() -> {
            node.setDisable(!enabled);
            if (enabled) node.getStyleClass().add("highlighted");
            else node.getStyleClass().remove("highlighted");
        });
    }
}
