package it.polimi.ingsw.client.view.gui.scene.gamecomponents;

import it.polimi.ingsw.client.control.state.Cloud;
import it.polimi.ingsw.client.view.gui.scene.ChangeListenerReferenceManager;
import it.polimi.ingsw.enums.PieceColor;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.ListExpression;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.function.BiConsumer;

import static it.polimi.ingsw.client.view.gui.AssetManager.getCloudPng;
import static it.polimi.ingsw.client.view.gui.AssetManager.getStudentPngs;
import static it.polimi.ingsw.client.view.gui.GUIUtils.runLaterIfNotOnFxThread;

/**
 * Layout manager for the cloud list
 */
public class CloudListManager {
    /**
     * The cloud's image asset size
     */
    private final static int CLOUD_SIZE = 80;
    /**
     * The student piece image asset's size
     */
    private final static int PIECE_SIZE = 20;

    /**
     * The reference manager
     */
    private final ChangeListenerReferenceManager refManager;
    /**
     * The Pane in which content will be injected
     */
    private final Pane cloudListPane;
    /**
     * A ListExpression containing the list of clouds to represent
     */
    private final ListExpression<Cloud> cloudList;
    /**
     * A BooleanExpression indicating whether the layout should be interactive
     */
    private final BooleanExpression isInteractive;
    /**
     * A click handler for when a cloud has been clicked
     */
    private final BiConsumer<MouseEvent, Integer> onCloudClick;

    /**
     * Creates a new instance with the new parameters
     *
     * @param cloudListPane the Pane in which content will be injected
     * @param cloudList     a ListExpression containing the list of clouds to represent
     * @param isInteractive a BooleanExpression indicating whether the layout should be interactive
     * @param onCloudClick  a click handler for when a cloud has been clicked
     * @throws IllegalArgumentException if any parameter is null
     */
    public CloudListManager(Pane cloudListPane,
                            ListExpression<Cloud> cloudList,
                            BooleanExpression isInteractive,
                            BiConsumer<MouseEvent, Integer> onCloudClick) {
        if (cloudListPane == null) throw new IllegalArgumentException("cloudListPane is null");
        if (cloudList == null) throw new IllegalArgumentException("cloudList is null");
        if (isInteractive == null) throw new IllegalArgumentException("isInteractive is null");
        if (onCloudClick == null) throw new IllegalArgumentException("onCloudClick is null");

        this.refManager = new ChangeListenerReferenceManager();
        this.cloudListPane = cloudListPane;
        this.cloudList = cloudList;
        this.isInteractive = isInteractive;
        this.onCloudClick = onCloudClick;
    }

    /**
     * Start reactively managing the layout.
     * <p>
     * Note: the absence of a stopManaging() here is intentional. The lifecycle of each GameState object is as long as
     * that of its view. Therefore, we have to worry only about internal cleanup between relayouts.
     */
    public void startManaging() {
        cloudList.addListener((ListChangeListener<Cloud>) change ->
                runLaterIfNotOnFxThread(() -> layout(change.getList())));
        isInteractive.addListener((obs, oldVal, newVal) ->
                runLaterIfNotOnFxThread(() -> enableAndHighlightIfTrue(cloudListPane, newVal)));
        layout(cloudList.get());
    }

    /**
     * Enables and highlights the Node if the given value is true
     *
     * @param node the Node
     * @param val  the value
     */
    private void enableAndHighlightIfTrue(Node node, boolean val) {
        node.setDisable(!val);
        if (val) node.getStyleClass().add("highlighted");
        else node.getStyleClass().remove("highlighted");
    }

    /**
     * Cleans the slate and injects up-to-date content into the root pane.
     *
     * @param clouds the cloud list
     */
    private void layout(ObservableList<? extends Cloud> clouds) {
        refManager.unregisterAll();
        cloudListPane.getChildren().clear();
        clouds.forEach(c -> cloudListPane.getChildren().add(makeCloud(c)));
    }

    /**
     * Creates a Node representing the given cloud
     *
     * @param cloud the cloud
     * @return a new Node
     */
    private Node makeCloud(Cloud cloud) {
        ImageView img = new ImageView(getCloudPng());
        img.setPreserveRatio(true);
        img.setFitHeight(CLOUD_SIZE);

        GridPane pieces = new GridPane();
        pieces.setPrefSize(CLOUD_SIZE, CLOUD_SIZE);
        pieces.setAlignment(Pos.CENTER);
        for (int i = 0; i < cloud.getStudents().length; i++)
            pieces.addRow(i % 2, makeStudent(cloud.getStudents()[i]));

        StackPane pane = new StackPane(img, pieces);
        pane.setOnMouseClicked(e -> onCloudClick.accept(e, cloud.getId()));
        refManager.registerListener(isInteractive, (observable, oldValue, newValue) ->
                runLaterIfNotOnFxThread(() -> pointerIfTrue(pane, newValue)));
        pointerIfTrue(pane, isInteractive.get());
        return pane;
    }

    /**
     * Adds a pointer mouse cursor to the Node if the value is true
     *
     * @param node the Node
     * @param val  the value
     */
    private void pointerIfTrue(Node node, boolean val) {
        if (val) node.setCursor(Cursor.HAND);
        else node.setCursor(Cursor.DEFAULT);
    }

    /**
     * Creates a student tile of the specified color
     *
     * @param color the student's color
     * @return a new Node
     */
    private Node makeStudent(PieceColor color) {
        ImageView img = new ImageView(getStudentPngs().get(color));
        img.setPreserveRatio(true);
        img.setFitHeight(PIECE_SIZE);
        GridPane.setHalignment(img, HPos.CENTER);
        GridPane.setValignment(img, VPos.CENTER);
        return img;
    }
}
