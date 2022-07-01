package it.polimi.ingsw.client.view.gui.scene.gamecomponents;

import it.polimi.ingsw.client.control.state.Board;
import it.polimi.ingsw.client.control.state.Professor;
import it.polimi.ingsw.client.view.gui.scene.ChangeListenerReferenceManager;
import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.functional.Tuple;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

import static it.polimi.ingsw.client.view.gui.AssetManager.*;
import static it.polimi.ingsw.client.view.gui.GUIUtils.runLaterIfNotOnFxThread;

/**
 * Layout manager for the board list.
 */
public class BoardListManager {
    /**
     * Spacing used by the HBox displaying the un-owned professors
     */
    private final static int UNBOUD_SPACING = 10;
    /**
     * Padding used by the HBox displaying the un-owned professors
     */
    private final static Insets UNBOUD_PADDING = new Insets(5);
    /**
     * Size of each unowned professors image
     */
    private final static int UNBOUND_SIZE = 40;

    /**
     * Margin between the owner's name and the board
     */
    private final static Insets OWNER_MARGIN = new Insets(5, 0, 0, 0);

    /**
     * Height of the last played assistant card
     */
    private final static int LAST_PLAYED_H = 150;
    /**
     * Margin between the last played assistant and the board
     */
    private final static Insets LAST_PLAYED_MARGIN = new Insets(0, 0, 0, 5);

    /**
     * Coin image asset size
     */
    private final static int COIN_SIZE = 50;
    /**
     * Position relative to the containing pane of the coin label (x-y)
     */
    private final static Tuple<Double, Double> COIN_LABEL_POS = new Tuple<>(25.0, 30.0);

    /**
     * Board image width
     */
    private final static int BOARD_W = 590;
    /**
     * Board image height
     */
    private final static int BOARD_H = 257;
    /**
     * Size of each student image asset
     */
    private final static int PIECE_SIZE = 25;

    /**
     * HGap used by the entrance's tile pane
     */
    private final static double ENTRANCE_HGAP = 8.5;
    /**
     * VGap used by the entrance's tile pane
     */
    private final static double ENTRANCE_VGAP = 15.5;
    /**
     * Size of the entrance tile pane (width-height)
     */
    private final static Tuple<Double, Double> ENTRANCE_SIZE = new Tuple<>(84.0, 227.0);
    /**
     * Padding used by the entrance tile pane
     */
    private final static Insets ENTRANCE_PADDING = new Insets(0, 5, 3, 0);

    /**
     * Vertical spacing between the different hall rows
     */
    private final static int HALL_V_SPACING = 17;
    /**
     * Size of the hall VBox (width-height)
     */
    private final static Tuple<Double, Double> HALL_V_SIZE = new Tuple<>(294.0, 200.0);
    /**
     * Padding used by the hall VBox
     */
    private final static Insets HALL_V_PADDING = new Insets(2, 0, 0, 8);
    /**
     * Position of the hall VBox relative to its container (x-y)
     */
    private final static Tuple<Double, Double> HALL_V_POS = new Tuple<>(102.0, 29.0);
    /**
     * Horizontal spacing used by each student row
     */
    private final static int HALL_H_SPACING = 3;

    /**
     * Spacing between each professor
     */
    private final static double PROF_SPACING = 12;
    /**
     * The padding used by the professor VBox
     */
    private final static Insets PROF_PADDING = new Insets(2, 0, 0, 0);
    /**
     * Size of each professor image asset
     */
    private final static int PROF_SIZE = 30;
    /**
     * The professors' vbox position relative to its container (x-y)
     */
    private final static Tuple<Double, Double> PROF_POS = new Tuple<>(416.0, 29.0);

    /**
     * The horizontal position of the tower relative to its parent
     */
    private final static double TOWER_POS_H = 505;
    /**
     * The height of the tower asset
     */
    private final static int TOWER_H = 50;

    /**
     * A list of piece colors ordered as the colors on the board's hall and professor table
     */
    private static final List<PieceColor> COLOR_ORDER =
            List.of(PieceColor.GREEN, PieceColor.RED, PieceColor.YELLOW, PieceColor.PINK, PieceColor.BLUE);

    /**
     * The reference manager
     */
    private final ChangeListenerReferenceManager refManager;
    /**
     * The pane to inject the dynamic content into
     */
    private final Pane boardListPane;
    /**
     * Boolean binding indicating whether the layout should be interactive
     */
    private final BooleanBinding areStudentsClickable;
    /**
     * The boards property
     */
    private final ListProperty<Board> boards;
    /**
     * The professors property
     */
    private final ListProperty<Professor> professors;
    /**
     * Handler for when a student is clicked (fired only when {@link #areStudentsClickable} is true)
     */
    private final BiConsumer<MouseEvent, PieceColor> onStudentClick;
    /**
     * The username of the player on this client
     */
    private final String myUsername;
    /**
     * Flag indicating whether the current match is in expert mode or not
     */
    private final boolean isExpertMode;

    /**
     * Creates a new instance with the given parameters
     *
     * @param boardListPane        The pane to inject the dynamic content into
     * @param myUsername           The username of the player on this client
     * @param isExpertMode         Flag indicating whether the current match is in expert mode or not
     * @param areStudentsClickable Boolean binding indicating whether the layout should be interactive
     * @param boards               The boards property
     * @param professors           The professors property
     * @param onStudentCLick       Handler for when a student is clicked
     * @throws IllegalArgumentException if any parameter is null
     */
    public BoardListManager(Pane boardListPane,
                            String myUsername,
                            boolean isExpertMode,
                            BooleanBinding areStudentsClickable,
                            ListProperty<Board> boards,
                            ListProperty<Professor> professors,
                            BiConsumer<MouseEvent, PieceColor> onStudentCLick) {
        if (boardListPane == null) throw new IllegalArgumentException("boardListPane shouldn't be null");
        if (myUsername == null) throw new IllegalArgumentException("myUsername shouldn't be null");
        if (areStudentsClickable == null) throw new IllegalArgumentException("areStudentsClickable shouldn't be null");
        if (boards == null) throw new IllegalArgumentException("boards shouldn't be null");
        if (professors == null) throw new IllegalArgumentException("professors shouldn't be null");

        this.refManager = new ChangeListenerReferenceManager();
        this.boardListPane = boardListPane;
        this.myUsername = myUsername;
        this.isExpertMode = isExpertMode;
        this.areStudentsClickable = areStudentsClickable;
        this.boards = boards;
        this.professors = professors;
        this.onStudentClick = onStudentCLick;
    }

    /**
     * Start reactively managing the layout.
     * <p>
     * Note: the absence of a stopManaging() here is intentional. The lifecycle of each GameState object is as long as
     * that of its view. Therefore, we have to worry only about internal cleanup between relayouts.
     */
    public void startManaging() {
        boards.addListener((ListChangeListener<Board>) change ->
                runLaterIfNotOnFxThread(() -> layout(professors.get(), change.getList())));
        professors.addListener((ListChangeListener<Professor>) change ->
                runLaterIfNotOnFxThread(() -> layout(change.getList(), boards.get())));

        layout(professors.get(), boards.get());
    }

    /**
     * Cleans the slate and injects up-to-date content into the root pane.
     *
     * @param professors the professors property
     * @param boards     the boards property
     */
    private void layout(ObservableList<? extends Professor> professors, ObservableList<? extends Board> boards) {
        refManager.unregisterAll();
        boardListPane.getChildren().clear();
        ObservableList<Node> nodes = boardListPane.getChildren();
        if (areThereUnboundProfessors(professors))
            nodes.add(makeUnboundProfessorList(professors));
        nodes.add(makeBoardList(professors, boards));
    }

    /**
     * Returns true if there are any un-owned professors in the given list
     *
     * @param professors the professor list
     * @return true if there are any un-owned professors in the given list
     */
    private boolean areThereUnboundProfessors(ObservableList<? extends Professor> professors) {
        return professors.stream().anyMatch(p -> p.getOwner() == null);
    }

    /**
     * Creates the node containing the un-owned professor list
     *
     * @param professors the complete professor list
     * @return a {@link Node}
     */
    private Node makeUnboundProfessorList(ObservableList<? extends Professor> professors) {
        HBox box = new HBox(UNBOUD_SPACING);
        box.setPadding(UNBOUD_PADDING);
        box.setAlignment(Pos.CENTER);
        professors.stream().filter(p -> p.getOwner() == null).forEach(p -> {
            ImageView prof = new ImageView(getProfessorPngs().get(p.getColor()));
            prof.setSmooth(true);
            prof.setPreserveRatio(true);
            prof.setFitHeight(UNBOUND_SIZE);
            box.getChildren().add(prof);
        });
        return box;
    }

    /**
     * Creates the node displaying the list of boards
     *
     * @param professors the complete professor list
     * @param boards     the board list
     * @return a {@link Node}
     */
    private Node makeBoardList(ObservableList<? extends Professor> professors, ObservableList<? extends Board> boards) {
        Node mainBoard = null;
        List<Node> secondaryBoards = new ArrayList<>(3);
        for (Board board : boards) {
            ObservableList<? extends Professor> myProfessors = professors.stream()
                    .filter(p -> Objects.equals(board.getUsername(), p.getOwner()))
                    .collect(FXCollections::observableArrayList, ObservableList::add, ObservableList::addAll);
            if (board.getUsername().equals(myUsername))
                mainBoard = makeBoard(1, myProfessors, board, true, onStudentClick);
            else
                secondaryBoards.add(makeBoard(0.7, myProfessors, board, false, null));
        }
        assert mainBoard != null;

        Group primaryBox = new Group(mainBoard);
        FlowPane secondaryBox = new FlowPane(secondaryBoards.toArray(new Node[]{}));
        secondaryBox.setAlignment(Pos.CENTER);
        secondaryBox.setOrientation(Orientation.VERTICAL);
        secondaryBox.setColumnHalignment(HPos.CENTER);
        secondaryBox.setRowValignment(VPos.CENTER);

        FlowPane box = new FlowPane(5, 0, primaryBox, secondaryBox);
        box.setAlignment(Pos.CENTER);
        box.setOrientation(Orientation.HORIZONTAL);
        box.setRowValignment(VPos.CENTER);
        box.setColumnHalignment(HPos.CENTER);

        return box;
    }

    /**
     * Creates a node representing only the given board
     *
     * @param scaling      the board scaling
     * @param myProfessors the list of professors placed on this board
     * @param board        the board
     * @param clickable    flag indicating whether the board should have interactivity
     * @param onClick      on click handler
     * @return a {@link Node}
     */
    private Node makeBoard(double scaling,
                           ObservableList<? extends Professor> myProfessors,
                           Board board,
                           boolean clickable,
                           BiConsumer<MouseEvent, PieceColor> onClick) {
        BorderPane root = new BorderPane();
        root.setBottom(makeOwnerLabel(board));
        root.setRight(makeRightNode(scaling, board));
        root.setCenter(makeBoardGraphics(scaling, myProfessors, board, clickable, onClick));
        return root;
    }

    /**
     * Creates the label containing this board's owner's name
     *
     * @param board the board
     * @return a {@link Node}
     */
    private Node makeOwnerLabel(Board board) {
        Label owner = new Label(board.getUsername());
        owner.getStyleClass().addAll(List.of("ery-label", "slightly-bigger"));
        owner.setAlignment(Pos.CENTER);
        owner.setOpacity(1);
        BorderPane.setAlignment(owner, Pos.CENTER);
        BorderPane.setMargin(owner, OWNER_MARGIN);
        return owner;
    }

    /**
     * Creates the node displayed on the right of the board
     *
     * @param scaling scaling
     * @param board   the board
     * @return a {@link Node}
     */
    private Node makeRightNode(double scaling, Board board) {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(makeLastPlayedAssistant(scaling, board));
        if (!isExpertMode)
            return box;

        ImageView coin = new ImageView(getCoinPng());
        coin.setPreserveRatio(true);
        coin.setFitHeight(COIN_SIZE * scaling);
        int count = board.getCoins();
        Label countLabel = new Label("x" + count);
        countLabel.getStyleClass().add("ery-label-inverted");
        countLabel.setOpacity(1);
        AnchorPane.setLeftAnchor(countLabel, COIN_LABEL_POS.getFirst() * scaling);
        AnchorPane.setTopAnchor(countLabel, COIN_LABEL_POS.getSecond() * scaling);

        AnchorPane coins = new AnchorPane(coin, countLabel);
        box.getChildren().add(coins);

        return box;
    }

    /**
     * Creates the node containing the last played assistant
     *
     * @param scaling the scaling
     * @param board   the board
     * @return a {@link Node}
     */
    private Node makeLastPlayedAssistant(double scaling, Board board) {
        AssistantType last = board.getLastPlayedAssistant();
        ImageView img = last == null
                ? new ImageView(getMagePngs().get(board.getMage()))
                : new ImageView(getAssistantPngs().get(last));
        img.setPreserveRatio(true);
        img.setFitHeight(LAST_PLAYED_H * scaling);
        BorderPane.setAlignment(img, Pos.CENTER);
        BorderPane.setMargin(img, scaleInset(LAST_PLAYED_MARGIN, scaling));
        return img;
    }

    /**
     * Creates the node containing the board's graphics and clickable elements, if it is clickable
     *
     * @param scaling      the scaling
     * @param myProfessors the list of professors placed on this board
     * @param board        the board
     * @param isClickable  a flag indicating whether this board should have interactivity
     * @param onClick      on click handler
     * @return a {@link Node}
     */
    private Node makeBoardGraphics(double scaling,
                                   ObservableList<? extends Professor> myProfessors,
                                   Board board,
                                   boolean isClickable,
                                   BiConsumer<MouseEvent, PieceColor> onClick) {
        ImageView boardGfx = new ImageView(getBoardPng());
        boardGfx.setFitWidth(BOARD_W * scaling);
        boardGfx.setFitHeight(BOARD_H * scaling);

        AnchorPane piecePane = new AnchorPane(
                makeEntrancePane(scaling, board, isClickable, onClick),
                makeHallPane(scaling, board),
                makeProfessorsPane(scaling, myProfessors),
                makeTowerPane(scaling, board));
        piecePane.setPrefWidth(BOARD_W * scaling);
        piecePane.setPrefWidth(BOARD_H * scaling);

        StackPane pane = new StackPane(boardGfx, piecePane);
        pane.setMaxWidth(BOARD_W * scaling);
        pane.setMaxHeight(BOARD_H * scaling);

        return pane;
    }

    /**
     * Creates the entrance section on the board
     *
     * @param scaling     the scaling
     * @param board       the board
     * @param isClickable a flag indicating whether this board should have interactivity
     * @param onClick     on click handler
     * @return a {@link Node}
     */
    private Node makeEntrancePane(double scaling, Board board, boolean isClickable, BiConsumer<MouseEvent, PieceColor> onClick) {
        TilePane entrancePane = new TilePane(ENTRANCE_HGAP * scaling, ENTRANCE_VGAP * scaling);
        entrancePane.setAlignment(Pos.BOTTOM_RIGHT);
        entrancePane.setOrientation(Orientation.HORIZONTAL);
        entrancePane.setPrefRows(2);
        entrancePane.setPrefColumns(5);
        entrancePane.setPrefWidth(ENTRANCE_SIZE.getFirst() * scaling);
        entrancePane.setPrefHeight(ENTRANCE_SIZE.getSecond() * scaling);
        entrancePane.setPadding(scaleInset(ENTRANCE_PADDING, scaling));
        AnchorPane.setTopAnchor(entrancePane, 0.0);
        AnchorPane.setLeftAnchor(entrancePane, 0.0);
        if (isClickable) {
            refManager.registerListener(areStudentsClickable, (_1, _2, v) -> highlightIfTrue(entrancePane, v));
            highlightIfTrue(entrancePane, areStudentsClickable.get());
        }
        Arrays.stream(board.getEntrance()).forEach(s -> {
            ImageView student = new ImageView(getStudentPngs().get(s));
            student.setPreserveRatio(true);
            student.setFitHeight(PIECE_SIZE * scaling);
            if (isClickable) {
                student.setOnMouseClicked(e -> onClick.accept(e, s));
                refManager.registerListener(areStudentsClickable, (_1, _2, v) -> enableIfTrue(student, v));
                enableIfTrue(student, areStudentsClickable.get());
            }
            entrancePane.getChildren().add(student);
        });
        return entrancePane;
    }

    /**
     * Highlight the given node if the value is true
     *
     * @param node        the {@link Node}
     * @param highlighted the boolean value
     */
    private void highlightIfTrue(Node node, boolean highlighted) {
        runLaterIfNotOnFxThread(() -> {
            if (highlighted) node.getStyleClass().add("highlighted");
            else node.getStyleClass().remove("highlighted");
        });
    }

    /**
     * Enable the node and displays a pointer if the value is true
     *
     * @param node    the node
     * @param enabled the value
     */
    private void enableIfTrue(Node node, boolean enabled) {
        runLaterIfNotOnFxThread(() -> {
            node.setDisable(!enabled);
            if (enabled) node.setCursor(Cursor.HAND);
            else node.setCursor(Cursor.DEFAULT);
        });
    }

    /**
     * Creates the hall section of the board
     *
     * @param scaling the scaling
     * @param board   the board
     * @return a {@link Node}
     */
    private Node makeHallPane(double scaling, Board board) {
        VBox box = new VBox(HALL_V_SPACING * scaling);
        box.setPrefWidth(HALL_V_SIZE.getFirst() * scaling);
        box.setPrefHeight(HALL_V_SIZE.getSecond() * scaling);
        box.setPadding(scaleInset(HALL_V_PADDING, scaling));
        AnchorPane.setLeftAnchor(box, HALL_V_POS.getFirst() * scaling);
        AnchorPane.setTopAnchor(box, HALL_V_POS.getSecond() * scaling);

        COLOR_ORDER.forEach(color -> {
            HBox row = new HBox(HALL_H_SPACING * scaling);
            row.setPrefHeight(PIECE_SIZE * scaling);
            long count = Arrays.stream(board.getHall()).filter(s -> s == color).count();
            for (int i = 0; i < count; i++) {
                ImageView student = new ImageView(getStudentPngs().get(color));
                student.setFitHeight(PIECE_SIZE * scaling);
                student.setPreserveRatio(true);
                row.getChildren().add(student);
            }
            box.getChildren().add(row);
        });
        return box;
    }

    /**
     * Creates the professor section of the board
     *
     * @param scaling      the scaling
     * @param myProfessors the list of professors places on this board
     * @return a {@link Node}
     */
    private Node makeProfessorsPane(double scaling, ObservableList<? extends Professor> myProfessors) {
        VBox box = new VBox(PROF_SPACING * scaling);
        box.setPadding(scaleInset(PROF_PADDING, scaling));
        AnchorPane.setLeftAnchor(box, PROF_POS.getFirst() * scaling);
        AnchorPane.setTopAnchor(box, PROF_POS.getSecond() * scaling);

        COLOR_ORDER.forEach(c -> {
            ImageView prof = new ImageView(getProfessorPngs().get(c));
            prof.setPreserveRatio(true);
            prof.setFitHeight(PROF_SIZE * scaling);
            prof.setRotate(90);
            prof.setVisible(myProfessors.stream().anyMatch(p -> p.getColor() == c));
            box.getChildren().add(prof);
        });
        return box;
    }

    /**
     * Crates the tower section of the board
     *
     * @param scaling the scaling
     * @param board   the board
     * @return a {@link Node}
     */
    private Node makeTowerPane(double scaling, Board board) {
        VBox box = new VBox();
        box.setPrefHeight(BOARD_H * scaling);
        AnchorPane.setLeftAnchor(box, TOWER_POS_H * scaling);
        box.setAlignment(Pos.CENTER);

        if (board.getTowers().length == 0)
            return box;

        ImageView tower = new ImageView(getTowerPngs().get(board.getTowers()[0]));
        tower.setFitHeight(TOWER_H * scaling);
        tower.setPreserveRatio(true);
        box.getChildren().add(tower);

        int count = board.getTowers().length;
        Label countLabel = new Label("x" + count);
        countLabel.getStyleClass().addAll("ery-label-inverted", "slightly-bigger");
        countLabel.setOpacity(1);
        box.getChildren().add(countLabel);

        return box;
    }

    /**
     * Creates a new {@link Insets} that is a scaled version of the given one
     *
     * @param orig  the starting inset
     * @param scale the scale factor
     * @return a new {@link Insets}
     */
    private Insets scaleInset(Insets orig, double scale) {
        return new Insets(
                orig.getTop() * scale,
                orig.getRight() * scale,
                orig.getBottom() * scale,
                orig.getLeft() * scale);
    }
}
