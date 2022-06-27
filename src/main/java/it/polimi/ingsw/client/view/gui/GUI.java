package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.control.Controller;
import it.polimi.ingsw.client.control.GUIControllerBridge;
import it.polimi.ingsw.client.control.state.GameState;
import it.polimi.ingsw.client.control.state.Lobby;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.gui.events.GameEndedEvent;
import it.polimi.ingsw.client.view.gui.events.RefreshLobbiesEvent;
import it.polimi.ingsw.client.view.gui.events.ShowErrorEvent;
import it.polimi.ingsw.client.view.gui.events.ToggleInputEvent;
import it.polimi.ingsw.client.view.gui.scene.GameSceneBuilder;
import it.polimi.ingsw.client.view.gui.scene.LobbyPhaseSceneBuilder;
import it.polimi.ingsw.client.view.gui.scene.MainMenuSceneBuilder;
import it.polimi.ingsw.client.view.gui.scene.PreparePhaseSceneBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static it.polimi.ingsw.client.view.gui.Strings.WIN_REASON;

/**
 * Implements the {@link View} interface for a GUI client.
 * <p>
 * Using {@link #launch}, the toolkit is initialized and the application is started in a new thread, since
 * {@link Application#launch(Class, String...)} blocks execution. Then each method communicates with the application
 * through {@link GuiApplication#afterInit(Consumer)}, which is a wrapper for {@link Platform#runLater(Runnable)}.
 */
public class GUI implements View {
    private final Controller controller;
    private final GUIControllerBridge bridge;
    private final MainMenuSceneBuilder mainMenu;

    private LobbyPhaseSceneBuilder lobbyPhase;
    private PreparePhaseSceneBuilder preparePhase;
    private GameSceneBuilder gameScene;

    /**
     * Creates a new GUI view given a Controller
     *
     * @param controller the {@link Controller} instance
     * @throws IllegalArgumentException if {@code controller} is null
     */
    public GUI(Controller controller) {
        if (controller == null) throw new IllegalArgumentException("controller shouldn't be null");
        this.controller = controller;

        bridge = new GUIControllerBridge(this.controller);
        mainMenu = new MainMenuSceneBuilder(bridge::sendFetch, bridge::sendCreate, bridge::sendJoin);
        lobbyPhase = null;
        preparePhase = null;
        gameScene = null;
    }

    /**
     * Launches the GUI interface in a new thread and then shows the main menu.
     */
    public void launch() {
        new Thread(() -> Application.launch(GuiApplication.class)).start();
        GuiApplication.afterInit(i -> i.connectWindowCloseCallback(e -> controller.setToEnd()));
    }

    /**
     * Displays the main menu of the game.
     */
    @Override
    public void showMainMenu() {
        GuiApplication.afterInit(i -> i.switchScene(mainMenu));
        bridge.sendFetch();
    }

    /**
     * Displays the fetched lobbies.
     */
    @Override
    public void showLobbies() {
        List<Lobby> lobbies = Arrays.stream(controller.getState().getLobbies())
                .filter(l -> l.getConnectedPlayers() < l.getNumPlayers())
                .toList();
        GuiApplication.afterInit(i -> Event.fireEvent(i.getRoot(), new RefreshLobbiesEvent(RefreshLobbiesEvent.REFESH, lobbies)));
    }

    /**
     * Displays the game's state.
     */
    @Override
    public void showGameState() {
        displayState();
        GuiApplication.afterInit(i ->
                i.getRoot().fireEvent(new ToggleInputEvent(ToggleInputEvent.DISABLE)));
    }

    /**
     * Displays the game's state during the user's turn.
     */
    @Override
    public void showPlayerTurnGameState() {
        displayState();
        GuiApplication.afterInit(i ->
                i.getRoot().fireEvent(new ToggleInputEvent(ToggleInputEvent.ENABLE)));
    }

    /**
     * Helper method that handles the presentation logic of a game
     */
    private void displayState() {
        createBuildersIfNecessary();
        GameState state = bridge.getGameState();
        GuiApplication.afterInit(instance -> {
            switch (state.getPhase()) {
                case "LobbyPhase" -> instance.switchSceneIfDifferent(lobbyPhase);
                case "PreparePhase" -> instance.switchSceneIfDifferent(preparePhase);
                default -> instance.switchSceneIfDifferent(gameScene);
            }
        });
    }

    /**
     * Helper method that lazy-initializes the needed builders
     */
    private void createBuildersIfNecessary() {
        if (lobbyPhase == null)
            lobbyPhase = new LobbyPhaseSceneBuilder(bridge.getGameState().playerListProperty(),
                    bridge.hasPendingUserMessagesProperty(),
                    bridge.isRejoiningProperty(),
                    bridge::sendLeave,
                    bridge::toMainMenu);
        if (preparePhase == null)
            preparePhase = new PreparePhaseSceneBuilder(bridge.getGameState().availableMagesProperty(),
                    bridge.hasPendingUserMessagesProperty(),
                    bridge.isRejoiningProperty(),
                    bridge::sendChooseMage,
                    bridge::toMainMenu);
        if (gameScene == null)
            gameScene = new GameSceneBuilder(bridge::toMainMenu, bridge);
    }

    /**
     * Displays the end state of the game.
     */
    @Override
    public void showEndGameState() {
        String reason = bridge.getGameEndReason();
        GameEndedEvent ev = reason.equals(WIN_REASON)
                ? new GameEndedEvent(GameEndedEvent.WIN, reason, bridge.getWinners())
                : new GameEndedEvent(GameEndedEvent.END, reason);
        GuiApplication.afterInit(i -> Event.fireEvent(i.getRoot(), ev));
        lobbyPhase = null;
        preparePhase = null;
        gameScene = null;
    }

    /**
     * Displays the error state of the game.
     */
    @Override
    public void showErrorState() {
        ShowErrorEvent ev = new ShowErrorEvent(ShowErrorEvent.ERROR, bridge.getLastErrorText());
        GuiApplication.afterInit(i -> Event.fireEvent(i.getRoot(), ev));
    }
}
