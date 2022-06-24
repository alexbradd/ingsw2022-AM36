package it.polimi.ingsw.client.view.gui.scene.gamecomponents;

import it.polimi.ingsw.client.control.state.Character;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.functional.Tuple;
import javafx.beans.binding.BooleanExpression;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.ListExpression;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import static it.polimi.ingsw.client.view.gui.AssetManager.*;
import static it.polimi.ingsw.client.view.gui.GUIUtils.runLaterIfNotOnFxThread;

/**
 * Layout manager for the list of available characters
 */
public class CharacterListManager {

    /**
     * The size of the character graphic
     */
    private final static Tuple<Integer, Integer> CHARACTER_SIZE = new Tuple<>(132, 200);
    /**
     * The size of the coin graphic
     */
    private final static int COIN_SIZE = 50;
    /**
     * The Y position of the piece TilePane relative to its container
     */
    private final static double PLACEABLES_Y = 50;
    /**
     * The size of each piece graphic
     */
    private final static int PIECE_SIZE = 30;

    /**
     * The Pane in which content will be injected
     */
    private final Pane characterListPane;
    /**
     * A ListExpression containing the list of characters to represent
     */
    private final ListExpression<Character> characterList;
    /**
     * A IntegerExpression containing the coins of the player on this client
     */
    private final IntegerExpression myCoins;
    /**
     * A BooleanExpression indicating whether the layout should be interactive
     */
    private final BooleanExpression isInteractive;
    /**
     * A click handler for when a character has been clicked
     */
    private final BiConsumer<MouseEvent, Character> clickHandler;

    /**
     * Creates a new instance with the new parameters
     *
     * @param characterListPane the Pane in which content will be injected
     * @param characterList     a ListExpression containing the list of characters to represent
     * @param myCoins           a IntegerExpression containing the coins of the player on this client
     * @param isInteractive     a BooleanExpression indicating whether the layout should be interactive
     * @param clickHandler      a click handler for when a character has been clicked
     * @throws IllegalArgumentException if any parameter is null
     */
    public CharacterListManager(Pane characterListPane,
                                ListExpression<Character> characterList,
                                IntegerExpression myCoins,
                                BooleanExpression isInteractive,
                                BiConsumer<MouseEvent, Character> clickHandler) {
        if (characterListPane == null) throw new IllegalArgumentException("characterListPane shouldn't be null");
        if (characterList == null) throw new IllegalArgumentException("characterList shouldn't be null");
        if (myCoins == null) throw new IllegalArgumentException("myCoins shouldn't be null");
        if (isInteractive == null) throw new IllegalArgumentException("isInteractive shouldn't be null");
        if (clickHandler == null) throw new IllegalArgumentException("clickHandler shouldn't be null");

        this.characterListPane = characterListPane;
        this.characterList = characterList;
        this.myCoins = myCoins;
        this.isInteractive = isInteractive;
        this.clickHandler = clickHandler;
    }

    /**
     * Start reactively managing the layout.
     * <p>
     * Note: the absence of a stopManaging() here is intentional. The lifecycle of each GameState object is as long as
     * that of its view. Therefore, we have to worry only about internal cleanup between relayouts.
     */
    public void startManaging() {
        characterList.addListener((ListChangeListener<Character>) change ->
                runLaterIfNotOnFxThread(() -> layout(change.getList())));
        isInteractive.addListener((obs, oldVal, newVal) ->
                runLaterIfNotOnFxThread(() -> enableAndHighlightIfTrue(characterListPane, newVal)));

        layout(characterList.get());
        enableAndHighlightIfTrue(characterListPane, isInteractive.get());
    }

    /**
     * Cleans the slate and injects up-to-date content into the root pane.
     *
     * @param characters the character list
     */
    private void layout(ObservableList<? extends Character> characters) {
        characterListPane.getChildren().clear();
        characters.forEach(c -> {
            Node character = makeCharacter(c);
            characterListPane.getChildren().add(character);
        });
    }

    /**
     * Creates the graphical redefinition of the given character
     *
     * @param character the character to draw
     * @return a Node
     */
    private Node makeCharacter(Character character) {
        CharacterType type = character.getType();
        ImageView img = new ImageView(getCharacterPngs().get(type));
        img.setPreserveRatio(true);
        img.setFitWidth(CHARACTER_SIZE.getFirst());
        img.setFitHeight(CHARACTER_SIZE.getSecond());

        AnchorPane overlay = new AnchorPane();

        if (character.getPrice() != type.getInitialCost())
            overlay.getChildren().add(makeCoin());
        if (type.hasStudents() || type.hasBlocks()) {
            Pane tilePane = makeTilePane();
            if (type.hasStudents())
                tilePane.getChildren().addAll(makeStudents(character.getStudents()));
            if (type.hasBlocks())
                tilePane.getChildren().add(makeBlocks(character.getBlocks()));
            overlay.getChildren().add(tilePane);
        }

        StackPane pane = new StackPane(img, overlay);
        pane.setOnMouseClicked(e -> clickHandler.accept(e, character));

        BooleanExpression clickable = isInteractive.and(myCoins.greaterThanOrEqualTo(character.getPrice()));
        pane.disableProperty().bind(clickable.not());
        clickable.addListener((obs, oldVal, newVal) -> runLaterIfNotOnFxThread(() -> pointerIfTrue(pane, newVal)));

        return pane;
    }

    /**
     * Creates the coin overlay
     *
     * @return a Node
     */
    private Node makeCoin() {
        ImageView img = new ImageView(getCoinPng());
        img.setPreserveRatio(true);
        img.setFitHeight(COIN_SIZE);
        img.setFitWidth(COIN_SIZE);

        AnchorPane.setTopAnchor(img, 0.0);
        AnchorPane.setRightAnchor(img, 0.0);
        return img;
    }

    /**
     * Creates the TilePane that will contain all the pieces placed on the card
     *
     * @return a TilePane
     */
    private TilePane makeTilePane() {
        TilePane tilePane = new TilePane();
        tilePane.setAlignment(Pos.CENTER);
        tilePane.setTileAlignment(Pos.CENTER);
        tilePane.setPrefColumns(4);
        tilePane.setPrefRows(2);
        AnchorPane.setTopAnchor(tilePane, PLACEABLES_Y);
        return tilePane;
    }

    /**
     * Creates the nodes representing the students placed on the card
     *
     * @param students the students placed on the card
     * @return a list of Nodes
     */
    private List<Node> makeStudents(PieceColor[] students) {
        ArrayList<Node> out = new ArrayList<>();
        HashMap<PieceColor, Integer> pieceCount = new HashMap<>();
        Arrays.stream(students).forEach(p -> pieceCount.merge(p, 1, Integer::sum));
        pieceCount.forEach((p, n) -> out.add(makeCountedImage(getStudentPngs().get(p), n)));
        return out;
    }

    /**
     * Creates the Node representing the amount of blocks placed on the card
     *
     * @param blocks the amount of blocks placed on the card
     * @return a Node
     */
    private Node makeBlocks(int blocks) {
        return makeCountedImage(getBlockPng(), blocks);
    }

    /**
     * Creates an image with the given count
     *
     * @param img   the image
     * @param count the number
     * @return a Node
     */
    private Node makeCountedImage(Image img, int count) {
        ImageView imageView = new ImageView(img);
        imageView.setFitWidth(PIECE_SIZE);
        imageView.setFitHeight(PIECE_SIZE);
        imageView.setPreserveRatio(true);

        Label countLabel = new Label("x" + count);
        countLabel.getStyleClass().add("ery-label");
        countLabel.setPrefWidth(PIECE_SIZE);
        countLabel.setAlignment(Pos.CENTER);
        countLabel.setOpacity(1);
        AnchorPane.setBottomAnchor(countLabel, 0.0);

        return new AnchorPane(imageView, countLabel);
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
     * Adds a pointer mouse cursor to the Node if the value is true
     *
     * @param node the Node
     * @param val  the value
     */
    private void pointerIfTrue(Node node, boolean val) {
        if (val) node.setCursor(Cursor.HAND);
        else node.setCursor(Cursor.DEFAULT);
    }
}
