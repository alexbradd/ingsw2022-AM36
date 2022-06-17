package it.polimi.ingsw.client.view.gui.scene;

import it.polimi.ingsw.client.view.gui.events.GameEndedEvent;
import it.polimi.ingsw.client.view.gui.events.ShowErrorEvent;
import it.polimi.ingsw.client.view.gui.events.ToggleInputEvent;
import it.polimi.ingsw.enums.Mage;
import javafx.application.Platform;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.util.Map;
import java.util.function.Consumer;

/**
 * FXML injected class that defines the behaviour of the various elements of the "choose a mage" screen.
 */
public class PreparePhaseSceneController {
    @FXML
    private StackPane rootPane;
    @FXML
    private HBox mageList;
    @FXML
    private Label notYourTurnLabel;
    @FXML
    private StackPane modalPane;
    @FXML
    private Label modalTitle;
    @FXML
    private Label modalText;
    @FXML
    private Button modalButton;
    @FXML
    private StackPane waitingResponsePane;

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
    private final Map<Mage, Image> mageImages;
    /**
     * A callback for when the mage has been selected by the user
     */
    private final Consumer<String> onChooseMage;
    /**
     * A callback for when the user clicks "to main menu" after a game has ended
     */
    private final Runnable afterEnd;

    /**
     * The ChangeHandler for when the mage list changes
     */
    private final ListChangeListener<Mage> mageListChangeListener = change ->
            Platform.runLater(() -> showMageList(change.getList()));
    /**
     * The change handler for when we are awaiting a server response
     */
    private final ChangeListener<Boolean> pendingMessagesListener = (obs, oldVal, newVal) ->
            Platform.runLater(() -> waitingResponsePane.setVisible(newVal));

    /**
     * Creates a new instance with the given resources
     *
     * @param availableMages     a binding containing all the pickable mages
     * @param hasPendingMessages a binding indicating whether the server has any pending messages
     * @param mageImages         a map associating each mage with its graphic
     * @param onChooseMage       a callback for when the mage has been selected by the user
     * @param afterEnd           a callback for when the user clicks "to main menu" after a game has ended
     * @throws IllegalArgumentException if any parameter is null
     */
    public PreparePhaseSceneController(ListBinding<Mage> availableMages,
                                       ReadOnlyBooleanProperty hasPendingMessages,
                                       Map<Mage, Image> mageImages,
                                       Consumer<String> onChooseMage,
                                       Runnable afterEnd) {
        if (availableMages == null) throw new IllegalArgumentException("availableMages shouldn't be null");
        if (hasPendingMessages == null) throw new IllegalArgumentException("hasPendingMessages shouldn't be null");
        if (mageImages == null) throw new IllegalArgumentException("mageImages shouldn't be null");
        if (onChooseMage == null) throw new IllegalArgumentException("onChooseMage shouldn't be null");
        if (afterEnd == null) throw new IllegalArgumentException("afterEnd shouldn't be null");

        this.availableMages = availableMages;
        this.hasPendingMessages = hasPendingMessages;
        this.mageImages = mageImages;
        this.onChooseMage = onChooseMage;
        this.afterEnd = afterEnd;
    }

    /**
     * Called by FXMLLoader
     */
    public void initialize() {
        WeakListChangeListener<Mage> listChangeListener = new WeakListChangeListener<>(mageListChangeListener);
        availableMages.addListener(listChangeListener);
        rootPane.addEventFilter(ShowErrorEvent.ERROR, e -> {
            modalTitle.setText("Error");
            modalText.setText(e.getErrorText());
            modalButton.setText("Close");
            modalButton.setOnMouseClicked(__ -> modalPane.setVisible(false));
            modalPane.setVisible(true);
        });
        rootPane.addEventFilter(GameEndedEvent.END, e -> {
            modalTitle.setText("Game ended");
            modalText.setText(e.getEndGameText());
            modalButton.setText("To mainMenu");
            modalButton.setOnMouseClicked(__ -> afterEnd.run());
            modalPane.setVisible(true);
        });
        rootPane.addEventFilter(ToggleInputEvent.ENABLE, e -> {
            notYourTurnLabel.setVisible(false);
            mageList.setOpacity(1);
            mageList.setDisable(false);
        });
        rootPane.addEventFilter(ToggleInputEvent.DISABLE, e -> {
            notYourTurnLabel.setVisible(true);
            mageList.setOpacity(0.5);
            mageList.setDisable(true);
        });
        showMageList(availableMages.get());
        hasPendingMessages.addListener(new WeakChangeListener<>(pendingMessagesListener));
    }

    /**
     * Helper method that displays all the mages contained in the given list
     *
     * @param list the list of available mages
     */
    private void showMageList(ObservableList<? extends Mage> list) {
        mageList.getChildren().clear();
        list.forEach(m -> mageList.getChildren().add(buildMageGraphic(m)));
    }

    /**
     * Helper method that builds the graphic container for a given mage
     *
     * @param mage the mage of which the component will be built
     * @return a ImageView
     */
    private ImageView buildMageGraphic(Mage mage) {
        Image img = mageImages.get(mage);
        assert img != null;
        ImageView graphic = new ImageView(img);
        graphic.setFitWidth(250);
        graphic.setFitHeight(250);
        graphic.setPreserveRatio(true);
        graphic.setCursor(Cursor.HAND);
        graphic.setOnMouseClicked(__ -> onChooseMage.accept(mage.toString()));
        return graphic;
    }
}
