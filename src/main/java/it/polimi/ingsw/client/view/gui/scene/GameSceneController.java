package it.polimi.ingsw.client.view.gui.scene;

import it.polimi.ingsw.client.control.GUIControllerBridge;
import it.polimi.ingsw.client.control.state.Board;
import it.polimi.ingsw.client.control.state.Character;
import it.polimi.ingsw.client.control.state.GameState;
import it.polimi.ingsw.client.view.gui.AssetManager;
import it.polimi.ingsw.client.view.gui.Strings;
import it.polimi.ingsw.client.view.gui.events.GameEndedEvent;
import it.polimi.ingsw.client.view.gui.events.ShowErrorEvent;
import it.polimi.ingsw.client.view.gui.events.ToggleInputEvent;
import it.polimi.ingsw.client.view.gui.scene.gamecomponents.*;
import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.PieceColor;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ListBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static it.polimi.ingsw.client.view.gui.GUIUtils.runLaterIfNotOnFxThread;

/**
 * FXML injected class that controls the behaviour of the main game scene
 */
public class GameSceneController {
    @FXML
    private HBox assistantList;
    @FXML
    private VBox boardList;
    @FXML
    private VBox boardsPane;
    @FXML
    private ListView<String> causeLogListView;
    @FXML
    private Label currentPlayer;
    @FXML
    private BorderPane errorEndDialog;
    @FXML
    private Spinner<Integer> islandIndexSpinner;
    @FXML
    private Group islandsGroup;
    @FXML
    private Button modalButton;
    @FXML
    private StackPane modalPane;
    @FXML
    private Label modalText;
    @FXML
    private Label modalTitle;
    @FXML
    private ChoiceBox<String> moveStudentChoiceBox;
    @FXML
    private Label studentColorLabel;
    @FXML
    private Button moveStudentButton;
    @FXML
    private BorderPane moveStudentDialog;
    @FXML
    private BorderPane moveMnDialog;
    @FXML
    private Spinner<Integer> motherNatureSpinner;
    @FXML
    private Button moveMnButton;
    @FXML
    private StackPane rootPane;
    @FXML
    private StackPane tablePane;
    @FXML
    private StackPane waitingPane;
    @FXML
    private Label waitingText;
    @FXML
    private ImageView sackImage;
    @FXML
    private FlowPane cloudPane;
    @FXML
    private TilePane characterPane;
    @FXML
    private BorderPane characterModal;
    @FXML
    private HBox characterTop;
    @FXML
    private StackPane characterCenter;

    /**
     * A callback executed after the "END" dialog has been closed
     */
    private final Runnable afterEnd;
    /**
     * The {@link GUIControllerBridge}
     */
    private final GUIControllerBridge bridge;
    /**
     * Convenience cache of the {@link GameState} as returned by {@link #bridge}.
     */
    private final GameState state;

    /**
     * The change handler controlling whether the "Waiting for response" should be visible or not
     */
    private final ChangeListener<Boolean> pendingUserMessageListener = (observable, oldVal, newVal) ->
            runLaterIfNotOnFxThread(() -> showHideWaitingPane(Strings.WAITING_RESPONSE, newVal));
    /**
     * The change handler for when we are waiting other players to join after the match has been pulled from disk
     */
    private final ChangeListener<Boolean> rejoiningListener = (obs, oldVal, newVal) ->
            runLaterIfNotOnFxThread(() -> showHideWaitingPane(Strings.WAITING_JOIN, newVal));

    /**
     * Creates a new instance with the given parameters
     *
     * @param afterEnd a callback executed after the "END" dialog has been closed
     * @param bridge   the {@link GUIControllerBridge} to use
     * @throws IllegalArgumentException if any parameter is null
     */
    public GameSceneController(Runnable afterEnd, GUIControllerBridge bridge) {
        if (afterEnd == null) throw new IllegalArgumentException("afterEnd shouldn't be null");
        if (bridge == null) throw new IllegalArgumentException("bridge shouldn't be null");

        this.afterEnd = afterEnd;
        this.bridge = bridge;
        this.state = bridge.getGameState();
    }

    /**
     * Invoked by FXMLLoader
     */
    public void initialize() {
        addModalEventFilters();
        addInputEnablementFilters();

        initMoveStudentModal();
        initMoveMnModal();

        setupCurrentPlayerLabel();
        setupCauseLog();
        setupSack();

        initBoardListManager();
        initAssistantListManager();
        initIslandListManager();
        initCloudListManager();
        if (bridge.isExpertMode()) initCharacterHandling();
    }

    /**
     * Adds various event filters and change listeners for controlling various informative modals
     */
    private void addModalEventFilters() {
        rootPane.addEventFilter(ShowErrorEvent.ERROR, e -> showErrorModal(e.getErrorText()));
        rootPane.addEventFilter(GameEndedEvent.END, e -> showEndModal(e.getEndGameText()));
        rootPane.addEventFilter(GameEndedEvent.WIN, e -> showWinModal(e.getWinners()));

        ReadOnlyBooleanProperty pending = bridge.hasPendingUserMessagesProperty(),
                rejoining = bridge.isRejoiningProperty();
        pending.addListener(new WeakChangeListener<>(pendingUserMessageListener));
        rejoining.addListener(new WeakChangeListener<>(rejoiningListener));
        if (pending.get())
            showHideWaitingPane(Strings.WAITING_RESPONSE, true);
        else if (rejoining.get())
            showHideWaitingPane(Strings.WAITING_JOIN, true);
    }

    /**
     * Helper that sets the visibility of the "waiting for..." pane showing the given text
     *
     * @param text the text to show
     * @param show the visibility boolean
     */
    private void showHideWaitingPane(String text, Boolean show) {
        waitingText.setText(text);
        waitingPane.setVisible(show);
    }

    /**
     * Shows an error modal with the given body
     *
     * @param body the error body
     */
    private void showErrorModal(String body) {
        showEndErrorModal("Error", body, "Close", this::closeModalAction);
    }

    /**
     * Shows a "Game ended" modal with the given body
     *
     * @param body the modal's body
     */
    private void showEndModal(String body) {
        showEndErrorModal("Game ended", body, "To main menu", __ -> afterEnd.run());
    }

    /**
     * Shows a win/loss modal for the given list of winners
     *
     * @param winners the list of winners
     */
    private void showWinModal(List<String> winners) {
        String body = String.join(", ", winners) + " won the game!";
        String title = winners.contains(bridge.getMyUsername())
                ? "You won!"
                : "You lost";
        showEndErrorModal(title, body, "To main menu", __ -> afterEnd.run());
    }

    /**
     * Shows the modal use for END and ERROR messages
     *
     * @param title        the title of the modal
     * @param body         the body of the modal
     * @param buttonText   the "close" button text
     * @param clickHandler the "close" button click handler
     */
    private void showEndErrorModal(String title, String body, String buttonText, EventHandler<MouseEvent> clickHandler) {
        modalTitle.setText(title);
        modalText.setText(body);
        modalButton.setText(buttonText);
        modalButton.setOnMouseClicked(clickHandler);

        errorEndDialog.setVisible(true);
        moveStudentDialog.setVisible(false);
        moveMnDialog.setVisible(false);
        characterModal.setVisible(false);

        modalPane.setVisible(true);
    }

    /**
     * Displays the "Move student" dialog
     *
     * @param color the color of the student that is being moved
     */
    private void showMoveStudentDialog(PieceColor color) {
        studentColorLabel.setText(color.toString());
        moveStudentButton.setOnMouseClicked(__ -> {
            switch (moveStudentChoiceBox.getValue()) {
                case "Hall" -> bridge.sendMoveStudent(color.toString());
                case "Island" -> bridge.sendMoveStudent(color.toString(), islandIndexSpinner.getValue());
                default -> showErrorModal("Invalid mode destination");
            }
            closeModalAction();
        });

        errorEndDialog.setVisible(false);
        moveMnDialog.setVisible(false);
        characterModal.setVisible(false);
        moveStudentDialog.setVisible(true);

        modalPane.setVisible(true);
    }

    /**
     * Shows the "Move mother nature dialog"
     */
    private void showMoveMnDialog() {
        errorEndDialog.setVisible(false);
        moveStudentDialog.setVisible(false);
        characterModal.setVisible(false);
        moveMnDialog.setVisible(true);

        modalPane.setVisible(true);
    }

    /**
     * Displays the character invocation dialog
     *
     * @param manager   the manager in charge of the dialog
     * @param character the character to display the modal for
     */
    private void showCharacterInvocationDialog(CharacterModalManager manager, Character character) {
        errorEndDialog.setVisible(false);
        moveStudentDialog.setVisible(false);
        moveMnDialog.setVisible(false);
        characterModal.setVisible(true);

        manager.showFor(character);

        modalPane.setVisible(true);
    }

    /**
     * Adds various event filters for controlling over all user input
     */
    private void addInputEnablementFilters() {
        rootPane.addEventFilter(ToggleInputEvent.ENABLE, __ -> {
            boardsPane.setDisable(false);
            tablePane.setDisable(false);
        });
        rootPane.addEventFilter(ToggleInputEvent.DISABLE, __ -> {
            boardsPane.setDisable(true);
            tablePane.setDisable(true);
        });
    }

    /**
     * Sets up the "Move student" dialog's bindings and default values
     */
    private void initMoveStudentModal() {
        moveStudentChoiceBox.setItems(FXCollections.observableList(List.of("Hall", "Island")));
        moveStudentChoiceBox.getSelectionModel().selectFirst();
        BooleanBinding disableSpinner = moveStudentChoiceBox.valueProperty().isEqualTo("Hall");
        islandIndexSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 11));
        islandIndexSpinner.disableProperty().bind(disableSpinner);
    }

    /**
     * Sets up the "Move student" dialog's bindings and default values
     */
    private void initMoveMnModal() {
        motherNatureSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
        moveMnButton.setOnMouseClicked(__ -> {
            bridge.sendMoveMn(motherNatureSpinner.getValue());
            closeModalAction();
        });
    }

    /**
     * Sets up the bindings for the "X's turn" label
     */
    private void setupCurrentPlayerLabel() {
        ChangeListener<String> listener = (observableValue, s, t1) ->
                runLaterIfNotOnFxThread(() -> currentPlayer.setText(t1));
        state.currentPlayerProperty().addListener(listener);
        currentPlayer.setText(state.currentPlayerProperty().get());
    }

    /**
     * Sets up the bindings for the user event log
     */
    private void setupCauseLog() {
        ObservableList<String> causes = state.getCauses();
        ObservableList<String> items = causes == null
                ? FXCollections.observableArrayList()
                : FXCollections.observableArrayList(causes);
        state.causesProperty().addListener((ListChangeListener<String>) change -> runLaterIfNotOnFxThread(() -> {
            while (change.next()) {
                if (change.wasAdded())
                    items.addAll(change.getAddedSubList());
            }
            causeLogListView.scrollTo(change.getList().size() - 1);
        }));
        causeLogListView.setItems(items);
        causeLogListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        causeLogListView.scrollTo(items.size() - 1);
    }

    /**
     * Sets up the various bindings controlling the sack's appearance
     */
    private void setupSack() {
        state.isSackEmptyProperty().addListener((obs, oldVal, newVal) -> runLaterIfNotOnFxThread(() -> {
            if (newVal) sackImage.setImage(AssetManager.getSackPngs().getFirst());
            else sackImage.setImage(AssetManager.getSackPngs().getSecond());
        }));
    }

    /**
     * Creates a new {@link BoardListManager} and tells it to start managing the corresponding pane
     */
    private void initBoardListManager() {
        BooleanBinding isInteractive = state.phaseProperty()
                .isEqualTo("StudentMovePhase")
                .and(state.currentPlayerProperty().isEqualTo(bridge.getMyUsername()));
        BoardListManager manager = new BoardListManager(boardList,
                bridge.getMyUsername(),
                bridge.isExpertMode(),
                isInteractive,
                state.boardsProperty(),
                state.professorsProperty(),
                (__, color) -> showMoveStudentDialog(color));
        manager.startManaging();
    }

    /**
     * Creates a new {@link AssistantListManager} and tells it to start managing the corresponding pane
     */
    private void initAssistantListManager() {
        ListBinding<AssistantType> assistantTypes = new ListBinding<>() {
            {
                super.bind(state.boardsProperty());
            }

            @Override
            protected ObservableList<AssistantType> computeValue() {
                Board myBoard = Arrays.stream(state.getBoards())
                        .filter(b -> b.getUsername().equals(bridge.getMyUsername()))
                        .findAny()
                        .orElseThrow(IllegalStateException::new);
                return FXCollections.observableList(Arrays.asList(myBoard.getAssistants()));
            }
        };
        BooleanBinding isInteractive = state.phaseProperty()
                .isEqualTo("PlanningPhase")
                .and(state.currentPlayerProperty()
                        .isEqualTo(bridge.getMyUsername()));
        AssistantListManager manager = new AssistantListManager(
                assistantList,
                isInteractive,
                assistantTypes,
                (__, t) -> bridge.sendPlayAssistant(t.toString()));
        manager.startManaging();
    }

    /**
     * Creates a new {@link IslandListManager} and tells it to start managing the corresponding pane
     */
    private void initIslandListManager() {
        BooleanBinding isInteractive = state.phaseProperty()
                .isEqualTo("MnMovePhase")
                .and(state.currentPlayerProperty().isEqualTo(bridge.getMyUsername()));
        IslandListManager manager = new IslandListManager(islandsGroup,
                isInteractive,
                state.islandsProperty(),
                state.motherNatureProperty(),
                __ -> showMoveMnDialog());
        manager.startManaging();
    }

    /**
     * Creates a new {@link CloudListManager} and tells it to start managing the corresponding pane
     */
    private void initCloudListManager() {
        BooleanBinding isInteractive = state.phaseProperty()
                .isEqualTo("CloudPickPhase")
                .and(state.currentPlayerProperty()
                        .isEqualTo(bridge.getMyUsername()));
        CloudListManager manager = new CloudListManager(
                cloudPane,
                state.cloudsProperty(),
                isInteractive,
                (__, i) -> bridge.sendPickCloud(i));
        manager.startManaging();
    }

    /**
     * Sets up management of the character card list and the relative invocation modal
     */
    private void initCharacterHandling() {
        BooleanBinding isStudentMove = state.phaseProperty().isEqualTo("StudentMovePhase");
        BooleanBinding isMnMove = state.phaseProperty().isEqualTo("MnMovePhase");
        BooleanBinding isCloudPick = state.phaseProperty().isEqualTo("CloudPickPhase");
        BooleanBinding isActionPhase = isStudentMove.or(isMnMove).or(isCloudPick);
        BooleanBinding isInteractive = isActionPhase
                .and(state.currentPlayerProperty().isEqualTo(bridge.getMyUsername()))
                .and(state.usedCharacterProperty().not());
        IntegerBinding playerCoins = new IntegerBinding() {
            {
                super.bind(state.boardsProperty());
            }

            @Override
            protected int computeValue() {
                return Arrays.stream(state.getBoards())
                        .filter(b -> Objects.equals(b.getUsername(), bridge.getMyUsername()))
                        .map(Board::getCoins)
                        .findFirst()
                        .orElseThrow(IllegalStateException::new);
            }
        };

        CharacterModalManager modalManager = new CharacterModalManager(
                characterTop,
                characterCenter,
                (manager, tuple) -> {
                    tuple.consume(bridge::sendPlayCharacter);
                    manager.close();
                },
                this::closeModalAction);
        CharacterListManager listManager = new CharacterListManager(
                characterPane,
                state.charactersProperty(),
                playerCoins,
                isInteractive,
                (__, character) -> showCharacterInvocationDialog(modalManager, character));
        modalManager.initialize();
        listManager.startManaging();
    }

    /**
     * Closes the currently displayed modal
     *
     * @param ignored ignored mouse event
     */
    @FXML
    private void closeModalAction(MouseEvent ignored) {
        modalPane.setVisible(false);
    }

    /**
     * Convenience wrapper around {@link #closeModalAction(MouseEvent)} to avoid passing around null
     */
    private void closeModalAction() {
        closeModalAction(null);
    }
}
