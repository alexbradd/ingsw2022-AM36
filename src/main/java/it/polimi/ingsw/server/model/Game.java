package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.controller.commands.PlayCharacterCommand;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import java.util.List;

/**
 * This class is the main class of the game model, representing an instance of a game. Its state is contained inside the
 * {@link #currentPhase} attribute. Its main method is {@link #executeUserCommand(UserCommand)}, that allows updating
 * its internal state.
 *
 * @author Leonardo Bianconi
 * @see Phase
 * @see UserCommand
 */
public class Game {
    /**
     * The current {@link Phase} of the {@code Game}.
     */
    private Phase currentPhase;

    /**
     * The base constructor. It can create a 2 or 3 player game, with standard or expert rules, based on the arguments.
     *
     * @param nPlayers   the number of players
     * @param expertMode whether the game should be in expert mode or not
     */
    public Game(int nPlayers, boolean expertMode) throws IndexOutOfBoundsException {
        if (nPlayers != 2 && nPlayers != 3) throw new IndexOutOfBoundsException();

        GameParameters parameters;
        if (nPlayers == 2) parameters = GameParameters.twoPlayerGame(expertMode);
        else parameters = GameParameters.threePlayersGame(expertMode);

        currentPhase = new LobbyPhase(parameters);
    }

    /**
     * This method allows interfacing with the {@code Game} via a {@code UserCommand}, change its internal state (its
     * {@link #currentPhase}) and return the changes expressed via a {@link PhaseDiff} object. An exception is thrown
     * if the update leads to an illegal state of the model. This method represents the main point of contact between
     * the game model and the external world.
     *
     * @param command the {@link UserCommand} to execute
     * @return a {@link PhaseDiff} object representing the changes from the new game model state and the previous one
     * (if and only if the command leads to a valid state of the {@code Game})
     * @throws InvalidPlayerException             if it is not the specified player's turn
     * @throws InvalidCharacterParameterException if the parameters passed are wrong for the specified character (in
     *                                            case of a {@link PlayCharacterCommand})
     * @throws InvalidPhaseUpdateException        if this command leads to a wrong game state
     */
    public PhaseDiff executeUserCommand(UserCommand command) throws InvalidPlayerException, InvalidCharacterParameterException, InvalidPhaseUpdateException {
        Phase oldPhase = currentPhase;
        updatePhase(command);
        return oldPhase.compare(currentPhase);
    }

    /**
     * This method contains all the operation to be done before and after updating the {@code currentPhase}, along with
     * the update itself.
     *
     * @param command the {@link UserCommand} to execute
     * @throws InvalidPlayerException             if it is not the specified player's turn
     * @throws InvalidCharacterParameterException if the parameters passed are wrong for the specified character (in
     *                                            case of a {@link PlayCharacterCommand})
     * @throws InvalidPhaseUpdateException        if this command leads to a wrong game state
     */
    private void updatePhase(UserCommand command) throws InvalidPlayerException, InvalidCharacterParameterException, InvalidPhaseUpdateException {
        currentPhase = command.execute(currentPhase);
    }

    /**
     * Getter for the maximum number of players of this {@code Game}.
     *
     * @return the maximum number of players
     */
    public int getNPlayers() {
        return currentPhase.parameters.getnPlayers();
    }

    /**
     * Whether this {@code Game} has expert rules or not.
     *
     * @return whether this {@code Game} has expert rules or not
     */
    public boolean isExpertMode() {
        return currentPhase.parameters.isExpertMode();
    }

    /**
     * Whether this {@code Game} has ended or not.
     *
     * @return whether this {@code Game} has ended or not
     */
    public boolean isEnded() {
        return currentPhase.isFinal();
    }

    /**
     * Returns a List<{@link Player}> containing the winners of the game.
     *
     * @return a List<{@link Player}> containing the winners of the game
     */
    public List<Player> getWinners() {
        return currentPhase.getWinners();
    }
}