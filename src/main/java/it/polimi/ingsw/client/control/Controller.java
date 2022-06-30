package it.polimi.ingsw.client.control;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.ProgramOptions;
import it.polimi.ingsw.client.control.state.State;
import it.polimi.ingsw.client.view.cli.CLI;
import it.polimi.ingsw.client.view.View;
import it.polimi.ingsw.client.view.gui.GUI;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * The client side controller of the application.
 * Manages server-events: instructing the UI to display the changes sent by the server.
 * Manages user-messages: checking the correctness of the message, displaying so that it can be consumed and
 * sent to the server.
 *
 * @author Mattia Busso
 */
public class Controller {

    // UI

    /**
     * The view of the game, either {@code CLI} or {@code GUI}.
     */
    private View view;

    // Game's mock state

    /**
     * The game's mock state.
     */
    private final State state;

    // Status

    /**
     * The status of the application, used to check correctness of user events and to display the right view.
     */
    private Status status;

    /**
     * Flag that indicates if the user wants to quit the application or if the server is unresponsive.
     */
    private boolean toEnd;

    /**
     * Pending user message flag property.
     */
    private final SimpleBooleanProperty hasPendingUserMessages;

    /**
     * Consumer called when a user message is available
     */
    private Consumer<JsonObject> onUserMessageCallback;

    /**
     * Callback called when {@link #setToEnd()} is called
     */
    private Runnable onEndCallback;

    /**
     * Basic constructor.
     * Creates a new mock state of the game.
     */
    public Controller() {
        state = new State();
        hasPendingUserMessages = new SimpleBooleanProperty(false);
        onUserMessageCallback = __ -> {
        };
        onEndCallback = () -> {
        };
    }

    /**
     * Creates the game's UI given the chosen client's {@link it.polimi.ingsw.ProgramOptions.ProgramMode}.
     * Starts the UI thread.
     *
     * @param mode the client's {@code ProgramMode} chosen by the user.
     * @throws IllegalArgumentException if the given mode is not valid
     * @throws IllegalStateException if the game's UI is already initialized
     */
    public void initUI(ProgramOptions.ProgramMode mode) throws IllegalArgumentException, IllegalStateException {
        if(view != null) {
            throw new IllegalStateException("view already initialized");
        }
        if(mode == ProgramOptions.ProgramMode.SERVER) {
            throw new IllegalArgumentException("illegal given mode");
        }
        else {
            status = Status.INITIAL;
            if (mode == ProgramOptions.ProgramMode.CLIENT_CLI) {
                CLI cli = new CLI(this);
                new Thread(cli).start();
                view = cli;
            } else {
                GUI gui = new GUI(this);
                gui.launch();
                view = gui;
            }
        }
    }

    /**
     * Sets the callback for the controller end event. If null is passed, an empty callback is set.
     *
     * @param callback the callback to call on end
     */
    public void setOnEnd(Runnable callback) {
        onEndCallback = Objects.requireNonNullElse(callback, () -> {
        });
    }

    // Server events

    /**
     * Given the command received from the server, updates the game's state;
     * calls the view's methods to display the changes to the user.
     *
     * @param o the {@code JsonObject} corresponding to the message received from the server
     * @throws IllegalArgumentException if the server message is null
     * @throws JsonSyntaxException if the given string json object is not valid
     * @throws IllegalStateException if the game's UI has not been initialized yet
     */
    public synchronized void manageServerEvent(JsonObject o) throws JsonSyntaxException {

        if(view == null) throw new IllegalStateException("UI not initialized");
        if(o == null) throw new IllegalArgumentException("the server message shouldn't be null");

        String type = o.get("type").getAsString();

        if(type == null) {
            throw new JsonSyntaxException("json string not valid");
        }

        switch (o.get("type").getAsString()) {
            case "LOBBIES" -> {
                state.updateLobbies(o);
                view.showLobbies();
                status = Status.FETCHED_LOBBIES;
            }
            case "LEFT" -> {
                state.resetState();
                view.showMainMenu();
                status = Status.INITIAL;
            }
            case "UPDATE" -> {
                state.updateGameState(o);
                status = Status.IN_GAME;
                if(state.isPlayerTurn()) {
                    view.showPlayerTurnGameState();
                }
                else {
                    view.showGameState();
                }
            }
            case "END" -> {
                toEnd = state.updateEndState(o);
                view.showEndGameState();
                status = Status.END;
            }
            case "ERROR" -> {
                state.updateErrorState(o);
                view.showErrorState();
            }
            default -> throw new JsonSyntaxException("json string not valid");
        }
        hasPendingUserMessages.set(false);
    }

    // User events

    /**
     * Given the message received from the user, it either performs client side operations or
     * sets the {@code userMessage} to be consumed by the server. After, the {@link #onUserMessageCallback} is called.
     *
     * @param o the user-message
     * @throws IllegalStateException    if the game's UI has not been initialized yet
     * @throws IllegalArgumentException if the given user-message is null
     */
    public synchronized void manageUserMessage(JsonObject o) throws IllegalStateException, IllegalArgumentException, JsonSyntaxException {

        if(view == null) throw new IllegalStateException("UI not initialized");
        if(o == null) throw new IllegalArgumentException("The user-message shouldn't be null");

        hasPendingUserMessages.set(true);
        onUserMessageCallback.accept(o);

    }

    /**
     * Sets the application's state to the main menu.
     */
    public synchronized void toMainMenu() {
        view.showMainMenu();
        state.resetState();
        status = Status.INITIAL;
    }

    /**
     * Sets the application's state to be disconnected.
     */
    public synchronized void toDisconnectState() {
        if(view != null) {
            view.showDisconnectState();
            status = Status.DISCONNECT;
        }
        else {
            System.out.println("Connection's not available");
            System.out.println("Disconnecting..");
        }
    }

    /**
     * Sets the callback for the userMessage event. If null is passed, an empty callback is set.
     *
     * @param callback the callback to call on end
     */
    public void setOnUserMessage(Consumer<JsonObject> callback) {
        onUserMessageCallback = Objects.requireNonNullElse(callback, __ -> {
        });
    }

    // Status and getters

    /**
     * The status of the application.
     */
    public enum Status {
        INITIAL, FETCHED_LOBBIES, IN_GAME, END, DISCONNECT
    }

    /**
     * Returns the status of the application.
     *
     * @return the status of the application
     */
    public synchronized Status getStatus() {
        return status;
    }

    /**
     * Returns the state of the game.
     *
     * @return the state of the game
     */
    public synchronized State getState() {
        return state;
    }

    /**
     * Returns true if there are any messages that the server has not yet responded to
     *
     * @return true if there are any messages that the server has not yet responded to
     */
    public synchronized boolean isHasPendingUserMessages() {
        return hasPendingUserMessages.get();
    }

    /**
     * Returns the property tracking whether there are any messages that the server has not yet responded to
     *
     * @return the property tracking whether there are any messages that the server has not yet responded to
     */
    public synchronized SimpleBooleanProperty hasPendingUserMessagesProperty() {
        return hasPendingUserMessages;
    }

    /**
     * Returns {@code true} if the application is not be to be shut down, {@code false} otherwise
     *
     * @return {@code true} if the application is to keep running
     */
    public synchronized boolean toRun() {
        return !toEnd;
    }

    /**
     * Sets the application to be shutdown and calls the {@link #onEndCallback} callback.
     */
    public synchronized void setToEnd() {
        toEnd = true;
        onEndCallback.run();
    }
}
