package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.client.view.gui.scene.SceneBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The main JavaFX application class. Its methods are accessible through the instance obtainable as a parameter from
 * {@link #afterInit(Consumer)}, which ensures that the code is executed in the javafx thread after all initialization
 * has taken place.
 */
public class GuiApplication extends Application {
    /**
     * The window width
     */
    public static final double WINDOW_WIDTH = 1280;
    /**
     * The window height
     */
    public static final double WINDOW_HEIGHT = 720;

    /**
     * Instance reference saved in {@link #start(Stage)}
     */
    private static GuiApplication instance;
    /**
     * Lock object used to handle synchronization in {@link #afterInit(Consumer)}.
     */
    private static final Object initLock = new Object();
    /**
     * Flag to signal that initialization is finished. See {@link #start}
     */
    private static boolean initialized = false;

    /**
     * The name of the current scene
     */
    private String sceneName = null;
    /**
     * The application's stage
     */
    private Stage stage;

    /**
     * Getter for the root element of the current {@link Scene}
     *
     * @return the root element of the current {@link Scene}
     */
    public Parent getRoot() {
        return stage.getScene().getRoot();
    }

    /**
     * Returns the name of the current {@link Scene}. If a scene has never switched, the name will be null.
     *
     * @return the name of the current {@link Scene}
     */
    public String getSceneName() {
        return sceneName;
    }

    /**
     * Returns the application instance. If the application has not finished initialization, this method will return
     * null
     *
     * @return the application instance
     */
    public static GuiApplication getInstance() {
        return instance;
    }

    /**
     * Switches the currently displayed root {@link Parent} to the one provided by the given {@link SceneBuilder}. If
     * the loading fails, the error is logged and the operation aborted.
     *
     * @param builder the {@link SceneBuilder}
     * @throws IllegalArgumentException if {@code builder} is null
     */
    public void switchScene(SceneBuilder builder) {
        if (builder == null) throw new IllegalArgumentException("builder shouldn't be null");
        try {
            stage.getScene().setRoot(builder.build());
            sceneName = builder.getName();
        } catch (IOException e) {
            System.out.println("Unable to load scene");
            e.printStackTrace();
        }
    }

    /**
     * Switches the currently displayed root {@link Parent} to the one provided by the given {@link SceneBuilder} only
     * if the current {@link Scene} and the one provided by the given builder have different names. If the loading
     * fails, the error is logged and the operation aborted.
     *
     * @param builder the {@link SceneBuilder}
     * @throws IllegalArgumentException if {@code builder} is null
     */
    public void switchSceneIfDifferent(SceneBuilder builder) {
        if (builder == null) throw new IllegalArgumentException("builder shouldn't be null");
        if (Objects.equals(sceneName, builder.getName()))
            return;
        switchScene(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(Stage stage) {
        synchronized (initLock) {
            stage.setTitle("Eriantys");
            stage.setMinWidth(WINDOW_WIDTH);
            stage.setMinHeight(WINDOW_HEIGHT);
            stage.setMaxWidth(WINDOW_WIDTH);
            stage.setMaxHeight(WINDOW_HEIGHT);
            stage.setScene(new Scene(new StackPane(), WINDOW_WIDTH, WINDOW_HEIGHT));
            stage.show();
            instance = this;
            this.stage = stage;
            initialized = true;
            initLock.notifyAll();
        }
    }

    /**
     * Sets the {@code onCloseRequest} stage callback to the given handler.
     *
     * @param handler the {@link EventHandler}
     */
    public void connectWindowCloseCallback(EventHandler<WindowEvent> handler) {
        this.stage.setOnCloseRequest(handler);
    }

    /**
     * Runs the given runnable on the application thread after the application has finished initialization. The method
     * will block until initialization in complete. If the thread the method has been called from is interrupted, the
     * operation is discarded.
     *
     * @param func the {@link Runnable} to run
     * @see Platform#runLater(Runnable)
     */
    public static void afterInit(Consumer<GuiApplication> func) {
        if (func == null) throw new IllegalArgumentException("func cannot be null");
        try {
            synchronized (initLock) {
                while (!initialized)
                    initLock.wait();
                Platform.runLater(() -> func.accept(instance));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
