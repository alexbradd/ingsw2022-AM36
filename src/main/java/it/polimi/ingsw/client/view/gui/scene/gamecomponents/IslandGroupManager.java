package it.polimi.ingsw.client.view.gui.scene.gamecomponents;

import it.polimi.ingsw.client.control.state.IslandGroup;
import it.polimi.ingsw.client.view.gui.scene.ChangeListenerReferenceManager;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.functional.Tuple;
import javafx.beans.binding.BooleanExpression;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static it.polimi.ingsw.client.view.gui.AssetManager.*;
import static it.polimi.ingsw.client.view.gui.GUIUtils.runLaterIfNotOnFxThread;

/**
 * Manager for a single island group
 */
public class IslandGroupManager {
    /**
     * The width of the island image asset
     */
    private final static int ISLAND_PNG_W = 100;
    /**
     * The height of each piece image asset
     */
    private final static int PIECE_PNG_W = 45;
    /**
     * The width of the pieces anchor pane
     */
    private final static int ANCHOR_PANE_W = 186;
    /**
     * The height of the pieces anchor pane
     */
    private final static int ANCHOR_PANE_H = 130;
    /**
     * The position of the island images in the container (x-y)
     */
    private final static Tuple<Double, Double> IMG_POS = new Tuple<>(0.0, 15.0);
    /**
     * The position of the island id label in the container (x-y)
     */
    private final static Tuple<Double, Double> ID_POS = new Tuple<>(20.0, 15.0);
    /**
     * The HGap used in the pieces tile pane
     */
    private final static int PIECES_HGAP = 2;
    /**
     * The HGap used in the pieces tile pane
     */
    private final static int PIECES_VGAP = 2;
    /**
     * The y position of the pieces tile pane in the root container
     */
    private final static double PIECES_Y = 33.0;
    /**
     * The x position of the mother nature graphic in the container
     */
    private final static double MN_X = 70.0;

    /**
     * The reference manager
     */
    private final ChangeListenerReferenceManager refManager;
    /**
     * The island group that is represented
     */
    private final IslandGroup data;
    /**
     * Boolean expression indicating whether mother nature is on this island
     */
    private final BooleanExpression isMnOnIsland;
    /**
     * Boolean expression indicating whether mother nature is clickable
     */
    private final BooleanExpression isMnClickable;
    /**
     * Handler for when mother nature is clicked (called only when mother nature is clickable)
     */
    private final Consumer<MouseEvent> onMnClick;

    /**
     * Creates a new instance with the given parameters
     *
     * @param data          the island group to represent
     * @param isMnOnIsland  Boolean expression indicating whether mother nature is on this island
     * @param isMnClickable Boolean expression indicating whether mother nature is clickable
     * @param onMnClick     Handler for when mother nature is clicked
     */
    public IslandGroupManager(IslandGroup data,
                              BooleanExpression isMnOnIsland,
                              BooleanExpression isMnClickable,
                              Consumer<MouseEvent> onMnClick) {
        this.refManager = new ChangeListenerReferenceManager();
        this.data = data;
        this.isMnOnIsland = isMnOnIsland;
        this.isMnClickable = isMnClickable;
        this.onMnClick = onMnClick;
    }

    /**
     * Builds a new node at the given angle and injects all change handlers
     *
     * @param angle the angle at which the island is build
     * @return a new Node
     */
    public Node build(double angle) {
        StackPane pane = new StackPane(buildIslandsNode(angle), buildPiecesNode(), buildMnNode());
        pane.setPrefHeight(ANCHOR_PANE_H);
        return pane;
    }

    /**
     * Builds the node containing the island graphics
     *
     * @param angle the angle at which the placed
     * @return a new Node
     */
    private Node buildIslandsNode(double angle) {
        final HBox islands = new HBox();
        // Thanks int[] not being usable into Arrays.asList() and therefore blocking me from using
        // Collections.reverse() :)
        Consumer<Integer> f = i -> {
            ImageView img = imageViewOf(getIslandPng(), ISLAND_PNG_W);
            Label id = new Label(String.valueOf(data.getIds()[i]));
            id.getStyleClass().addAll(List.of("ery-label-inverted", "slightly-bigger"));
            id.setOpacity(1);
            AnchorPane pane = new AnchorPane(img, id);

            AnchorPane.setLeftAnchor(img, IMG_POS.getFirst());
            AnchorPane.setTopAnchor(img, IMG_POS.getSecond());
            AnchorPane.setLeftAnchor(id, ID_POS.getFirst());
            AnchorPane.setTopAnchor(id, ID_POS.getSecond());

            islands.getChildren().add(pane);
        };

        if (angle > 0 && angle <= 180)
            for (int i = data.getIds().length - 1; i >= 0; i--)
                f.accept(i);
        else
            for (int i = 0; i < data.getIds().length; i++)
                f.accept(i);
        islands.setAlignment(Pos.CENTER);

        return islands;
    }

    /**
     * Creates the node containing all the information about the pieces placed on the island
     *
     * @return a new Node
     */
    private Node buildPiecesNode() {
        TilePane tilePane = new TilePane(PIECES_HGAP, PIECES_VGAP);
        tilePane.setPrefColumns(4);
        tilePane.setPrefRows(5);
        tilePane.setAlignment(Pos.CENTER);
        ObservableList<Node> pieces = tilePane.getChildren();
        for (PieceColor color : PieceColor.values()) {
            long count = Arrays.stream(data.getStudents()).filter(s -> s.equals(color)).count();
            pieces.add(countedImage(getStudentPngs().get(color), count));
        }
        if (data.getTowers().length > 0)
            pieces.add(countedImage(getTowerPngs().get(data.getTowers()[0]), data.getTowers().length));
        if (data.getBlocks() > 0)
            pieces.add(countedImage(getBlockPng(), data.getBlocks(), false));
        AnchorPane piecesAnchorPane = new AnchorPane(tilePane);
        piecesAnchorPane.setMaxWidth(ANCHOR_PANE_W);
        piecesAnchorPane.setMaxHeight(ANCHOR_PANE_H);
        AnchorPane.setTopAnchor(tilePane, PIECES_Y);
        return piecesAnchorPane;
    }

    /**
     * Creates the node in which mother nature is displayed
     *
     * @return a new Node
     */
    private Node buildMnNode() {
        ImageView mn = imageViewOf(getMnPng(), PIECE_PNG_W);
        refManager.registerListener(isMnClickable, (_1, _2, v) -> {
            showPointerIfTrue(mn, v);
            enableIfTrue(mn, v);
        });
        showPointerIfTrue(mn, isMnClickable.get());
        enableIfTrue(mn, isMnClickable.getValue());
        mn.setOnMouseClicked(onMnClick::accept);
        AnchorPane.setLeftAnchor(mn, MN_X);

        AnchorPane mnPane = new AnchorPane(mn);
        mnPane.setMaxWidth(ANCHOR_PANE_W);
        mnPane.setMaxHeight(ANCHOR_PANE_H);
        refManager.registerListener(isMnOnIsland, (_1, _2, v) -> visibleIfTrue(mnPane, v));
        refManager.registerListener(isMnClickable, (_1, _2, v) -> highlightIfTrue(mnPane, v));
        visibleIfTrue(mnPane, isMnOnIsland.get());
        highlightIfTrue(mnPane, isMnClickable.get());
        return mnPane;
    }

    /**
     * Show a pointer on the node if the value is true
     *
     * @param node    the node
     * @param pointer the value
     */
    private void showPointerIfTrue(Node node, boolean pointer) {
        runLaterIfNotOnFxThread(() -> {
            if (pointer)
                node.setCursor(Cursor.HAND);
            else
                node.setCursor(Cursor.DEFAULT);
        });
    }

    /**
     * Enable the node if the value is true
     *
     * @param node   the node
     * @param enable the value
     */
    private void enableIfTrue(Node node, boolean enable) {
        runLaterIfNotOnFxThread(() -> node.setDisable(!enable));
    }

    /**
     * Highlight the node if the value is true
     *
     * @param node        the node
     * @param highlighted the value
     */
    private void highlightIfTrue(Node node, boolean highlighted) {
        runLaterIfNotOnFxThread(() -> {
            if (highlighted)
                node.getStyleClass().add("highlighted");
            else
                node.getStyleClass().remove("highlighted");
        });
    }

    /**
     * Set the node as visible if the value is true
     *
     * @param node    the node
     * @param visible the value
     */
    private void visibleIfTrue(Node node, boolean visible) {
        runLaterIfNotOnFxThread(() -> node.setVisible(visible));
    }

    /**
     * Creates an image view of the given image with the given size
     *
     * @param png  the {@link Image}
     * @param size the size
     * @return a new {@link ImageView}
     */
    private ImageView imageViewOf(Image png, int size) {
        ImageView view = new ImageView(png);
        view.setPreserveRatio(true);
        view.setFitHeight(size);
        return view;
    }

    /**
     * Creates a new image with a quantity counter in white
     *
     * @param png   the {@link Image}
     * @param count the counter
     * @return a new Node
     */
    private Node countedImage(Image png, long count) {
        return countedImage(png, count, true);
    }

    /**
     * Creates a new image with a quantity counter
     *
     * @param png       the {@link Image}
     * @param count     the counter
     * @param textBlack true if the text should be black, false otherwise
     * @return a new Node
     */
    private Node countedImage(Image png, long count, boolean textBlack) {
        ImageView view = imageViewOf(png, PIECE_PNG_W);
        Label label = textBlack
                ? countLabel(count)
                : countLabelWhite(count);
        AnchorPane viewContainer = new AnchorPane(view, label);
        AnchorPane.setBottomAnchor(label, 0.0);
        return viewContainer;
    }

    /**
     * Creates the count label for a given image
     *
     * @param count the number to display
     * @return a {@link Label}
     */
    private Label countLabel(long count) {
        Label l = new Label("x" + count);
        l.setPrefWidth(PIECE_PNG_W);
        l.setAlignment(Pos.CENTER);
        l.setOpacity(1);
        l.getStyleClass().add("ery-label-inverted");
        l.getStyleClass().add("slightly-bigger");
        return l;
    }

    /**
     * Creates the count label for a given image, but in white
     *
     * @param count the number to display
     * @return a {@link Label}
     */
    private Label countLabelWhite(long count) {
        Label l = countLabel(count);
        l.getStyleClass().replaceAll(s -> {
            if (s.equals("ery-label-inverted"))
                return "ery-label";
            return s;
        });
        return l;
    }

    /**
     * Disconnect the change handlers from all the nodes built by this instance of the manager
     */
    public void disconnectListeners() {
        refManager.unregisterAll();
    }
}
