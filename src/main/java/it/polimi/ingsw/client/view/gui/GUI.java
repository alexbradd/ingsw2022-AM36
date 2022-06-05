package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.control.Controller;
import it.polimi.ingsw.client.control.GUIControllerBridge;
import it.polimi.ingsw.client.control.state.Lobby;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.gui.events.RefreshLobbiesEvent;
import it.polimi.ingsw.client.view.gui.events.ShowErrorEvent;
import it.polimi.ingsw.client.view.gui.scene.MainMenuSceneBuilder;
import it.polimi.ingsw.client.view.gui.scene.SceneBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

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
        List<Lobby> lobbies = Arrays.asList(controller.getState().getLobbies());
        GuiApplication.afterInit(i -> Event.fireEvent(i.getRoot(), new RefreshLobbiesEvent(RefreshLobbiesEvent.REFESH, lobbies)));
    }

    /**
     * Displays the game's state.
     */
    @Override
    public void showGameState() {
        System.out.println("Showing another player's turn state");
    }

    /**
     * Displays the game's state during the user's turn.
     */
    @Override
    public void showPlayerTurnGameState() {
        System.out.println("Showing your turn's state");
    }

    /**
     * Displays the end state of the game.
     */
    @Override
    public void showEndGameState() {
        GameEndedEvent ev = new GameEndedEvent(GameEndedEvent.END, bridge.getGameEndReason());
        GuiApplication.afterInit(i -> Event.fireEvent(i.getRoot(), ev));
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
