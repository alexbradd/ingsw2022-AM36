package it.polimi.ingsw.client.view.cli;

import com.google.gson.JsonObject;
import it.polimi.ingsw.client.control.CLIMessageBuilder;
import it.polimi.ingsw.client.control.Controller;
import it.polimi.ingsw.client.control.state.*;
import it.polimi.ingsw.client.view.View;

import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;

/**
 * This class implements a CLI representation of a client's view.
 * Each method represents a state of the program displayed to the user, with the relative commands available.
 *
 * @see View
 * @author Mattia Busso
 */
public class CLI implements View, Runnable {

    /**
     * The client side controller
     */
    private final Controller controller;

    /**
     * Basic constructor.
     * Takes the client side controller as a parameter.
     *
     * @param controller the client side controller
     * @throws IllegalArgumentException if {@code controller == null}
     */
    public CLI(Controller controller) throws IllegalArgumentException {
        if(controller == null) {
            throw new IllegalArgumentException("controller shouldn't be null");
        }
        this.controller = controller;
    }

    /**
     * User thread.
     * Receive commands from the user, builds the corresponding message and passes it to the controller.
     */
    public void run() {
        Scanner stdin = new Scanner(System.in);
        while(controller.toRun()) {
            Optional<JsonObject> message = CLIMessageBuilder.buildMessage(stdin, controller);
            message.ifPresent(jsonObject -> controller.manageUserMessage(message.get()));
        }
        stdin.close();
    }

    /**
     * Displays the initial message of the game.
     * Displays the available commands.
     */
    public void showMainMenu() {
        System.out.println("Welcome to Eryantis!");
        System.out.println("Please type one of the following commands to get started:");
        System.out.println("* FETCH (to fetch the current available lobbies)");
        System.out.println("* CREATE (to create a new lobby)");
        System.out.println("If you want to exit the application, you can type QUIT anytime to do so");
        System.out.println();
    }

    /**
     * Displays the fetched lobbies.
     * Displays the available commands.
     */
    public void showLobbies() {
        Lobby[] lobbies = controller.getState().getLobbies();

        if(lobbies.length == 0) {
            System.out.println("There are no lobbies available at the moment");
        }
        else {
            System.out.println("\nLobbies:\n");
            for (Lobby l : lobbies) {
                System.out.println(l);
            }
        }

        System.out.println("\nPlease type one of the following commands:");
        System.out.print(lobbies.length != 0 ? "* JOIN (to start joining an existing lobby)\n" : "");
        System.out.println("* BACK (to return to the main menu)");
        System.out.println();
    }

    /**
     * Displays the current state of the game.
     */
    public void showGameState() {
        clearTerminal();
        GameState gameState = controller.getState().getGameState();
        System.out.println(gameState);
        if(gameState.getPhase().equals("LobbyPhase")) {
            System.out.println("Please type one of the following commands:");
            System.out.println("* LEAVE (if you want to leave the lobby and return to the main menu)");
        }
        System.out.println();
    }

    /**
     * Displays the current state of the game.
     * Displays the available commands.
     */
    public void showPlayerTurnGameState() {
        clearTerminal();
        GameState gameState = controller.getState().getGameState();
        System.out.println(gameState);
        if ("PreparePhase".equals(gameState.getPhase())) {
            System.out.println("Available mages: " + Arrays.toString(gameState.getAvailableMages().toArray()));
            System.out.println("Please type the name of the mage you want to use");
        }
        else if("PlanningPhase".equals(gameState.getPhase())) {
            System.out.println("Available assistants: " + Arrays.toString(gameState.getPlayerAssistants()));
            System.out.print("Please type the name of the assistant you want to use");
        }
        else {
            switch (gameState.getPhase()) {
                case "StudentMovePhase" -> System.out.println("Please type the color of the student you want to move");
                case "MnMovePhase" -> System.out.println("Please type the number of times mother nature has to move");
                case "CloudPickPhase" -> System.out.println("Please type the id of the cloud you want choose");
            }
            if (controller.getState().getGameInfo().isExpert()) {
                System.out.println("or type a name of a character (if you want to play a character)");
            }
        }
        System.out.println();
    }

    /**
     * Displays the end message of the game.
     * Displays the available commands.
     */
    public void showEndGameState() {
        EndState endState = controller.getState().getEndState();
        System.out.println(endState);
        System.out.println("Please type one of the following commands:");
        System.out.println("* BACK (if you want to go back to the main menu)");
        System.out.println("* QUIT (if you want to exit the game)");
        System.out.println();
    }

    /**
     * Displays the error message of the game.
     */
    public void showErrorState() {
        ErrorState errorState = controller.getState().getErrorState();
        System.out.println(errorState);
        System.out.println();
    }

    /**
     * Clears the terminal using the ANSI escape code characters.
     * Doesn't work on all terminals.
     */
    private void clearTerminal() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}
