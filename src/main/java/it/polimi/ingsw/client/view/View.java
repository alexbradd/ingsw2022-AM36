package it.polimi.ingsw.client.view;

/**
 * The view interface provides methods used to display the various states of the game to the user.
 *
 * @see it.polimi.ingsw.client.view.cli.CLI
 * @see it.polimi.ingsw.client.view.gui.GUI
 * @author Mattia Busso
 */
public interface View {

    /**
     * Displays the main menu of the game.
     */
    void showMainMenu();

    /**
     * Displays the fetched lobbies.
     */
    void showLobbies();

    /**
     * Displays the game's state.
     */
    void showGameState();

    /**
     * Displays the game's state during the user's turn.
     */
    void showPlayerTurnGameState();

    /**
     * Displays the end state of the game.
     */
    void showEndGameState();

    /**
     * Displays the error state of the game.
     */
    void showErrorState();

    /**
     * Displays the disconnect state of the application.
     */
    void showDisconnectState();

}
