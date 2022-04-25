package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.Mage;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;

import java.util.List;
import java.util.function.Function;

/**
 * The Phase class represents a single state of the game, and it is a facade facing the controller via the Command
 * pattern, showing the available operations (its methods), that interact with the internal state of all model entities.
 * <p>
 * The class is completely immutable. Each modifier creates a new object; for more details see subclasses.
 *
 * @author Leonardo Bianconi, Alexandru Gabriel Bradatan
 * @see LobbyPhase
 * @see PreparePhase
 * @see PlanningPhase
 * @see ActionPhase
 * @see EndgamePhase
 */
public abstract class Phase {

    /**
     * The game's parameters.
     */
    protected final GameParameters parameters;

    /**
     * {@link GameParameters} constructor.
     *
     * @param parameters the game's parameters
     * @throws IllegalArgumentException if {@code parameters == null}
     */
    Phase(GameParameters parameters) throws IllegalArgumentException {
        if (parameters == null) {
            throw new IllegalArgumentException("parameters shouldn't be null");
        }
        this.parameters = parameters;
    }

    /**
     * Copy constructor: copies this Phase's GameParameters
     *
     * @param old the phase to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    Phase(Phase old) {
        if (old == null) throw new IllegalArgumentException("old shouldn't be null");
        this.parameters = old.parameters;
    }

    /**
     * Getter for this Phase's GameParameters
     *
     * @return this Phase's GameParameters
     */
    GameParameters getParameters() {
        return parameters;
    }

    /**
     * Getter for this Phase's {@link Table}.
     *
     * @return this Phase's {@link Table}.
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     */
    Table getTable() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks if the player with the given username exists and has permission to modify the game state.
     *
     * @param username the username of the player to authorize
     * @return a {@link Player} object
     * @throws IllegalArgumentException      if {@code username} is null
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws InvalidPlayerException        either if the specified player username is invalid or it is not the
     *                                       specified player's turn
     */
    public Player authorizePlayer(String username) throws InvalidPlayerException {
        throw new UnsupportedOperationException();
    }

    /**
     * Applies the given update to the {@link StudentContainer} of the {@link Island} with the given index.
     *
     * @param player the {@link Player} that will execute this operation
     * @param index  the index of the {@link Island}
     * @param update the update to apply
     * @return a new Phase containing the update
     * @throws IllegalArgumentException    if any parameter is null
     * @throws InvalidPhaseUpdateException if the index is out of bounds
     */
    public Phase updateIsland(Player player, int index, Function<StudentContainer, StudentContainer> update) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieve a Student from the specified Player's entrance
     *
     * @param player the Player of whom board to modify
     * @param color  the color to get from the entrance
     * @return a {@link Tuple} containing a Phase with the changes applied and the Student extracted
     * @throws InvalidPhaseUpdateException if the player's entrance doesn't have enough students of the specified color
     * @throws IllegalArgumentException    if any parameter is null
     */
    public Tuple<? extends Phase, Student> getFromEntrance(Player player, PieceColor color) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException();
    }

    /**
     * Applies the given update to the Hall of the given {@link Player}.
     *
     * @param player the {@link Player} of whom the Hall will be updated
     * @param update the update to apply
     * @return a new Phase containing the update
     * @throws IllegalArgumentException if any parameter is null
     */
    public Phase updateHall(Player player, Function<Hall, Hall> update) {
        throw new UnsupportedOperationException();
    }

    /**
     * Marks that the given {@link Player} has done one of his allowed movements.
     *
     * @param player the {@link Player} that has done the movements
     * @return a new Phase containing the update
     * @throws IllegalArgumentException if any parameter is null
     */
    public Phase markStudentMove(Player player) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException();
    }

    /**
     * This method lets the given Player choose the mage deck corresponding to the given enum.
     *
     * @param player a reference to a Player as returned by {@code authorizePlayer}
     * @param mage   a {@link Mage} value
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      any parameter is null
     * @throws InvalidPhaseUpdateException   if the mage has already been chosen by another player
     */
    public Phase chooseMageDeck(Player player, Mage mage) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException();
    }

    /**
     * This method lets the given Player play the assistant card of his deck corresponding the AssistantType passed.
     *
     * @param player    a reference to a Player as returned by {@code authorizePlayer}
     * @param assistant the AssistantType to play
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      if any parameter is null
     * @throws InvalidPhaseUpdateException   if this assistant has already been played by another player in this round
     * @throws InvalidPhaseUpdateException   if this assistant has already been played by the player
     */
    public Phase playAssistant(Player player, AssistantType assistant) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException();
    }

    /**
     * This method lets the given Player a movement of Mother Nature across the islands, for a total number of steps
     * specified via parameter. The movement is performed clockwise on the islands (see game rules).
     *
     * @param player a reference to a Player as returned by {@code authorizePlayer}
     * @param steps  the number of steps of the movement
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      if any parameter is null
     * @throws InvalidPhaseUpdateException   if the number of steps is invalid (see game rules)
     */
    public Phase moveMn(Player player, int steps) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException();
    }

    /**
     * This method lets the given Player play a character card. The {@link CharacterType} is
     * passed via parameter, along with all the needed additional information, as can be seen below:
     *
     * @param player    a reference to a Player as returned by {@code authorizePlayer}
     * @param character the {@link CharacterType} to play
     * @param steps     additional arguments that specify the behaviour of the card (see method description)
     * @throws UnsupportedOperationException      if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException           if any parameter is null
     * @throws InvalidCharacterParameterException if the additional information "args" is either missing or incorrect
     * @throws InvalidPhaseUpdateException        if the number of coins of the player is less than the amount required
     *                                            to play the character card (see game rules)
     */
    public Phase playCharacter(Player player, CharacterType character, CharacterStep[] steps) throws InvalidPhaseUpdateException, InvalidCharacterParameterException {
        throw new UnsupportedOperationException();
    }

    /**
     * This method lets the given Player pick a cloud from the playing area and retrieve all the students placed on it
     * (see game rules).
     *
     * @param id     the id of the id
     * @param player a reference to a Player as returned by {@code authorizePlayer}
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      if any parameter is null
     * @throws IndexOutOfBoundsException     if the id does not represent a valid id
     * @throws InvalidPhaseUpdateException   if the specified id has already been picked this round
     */
    public Phase drainCloud(Player player, int id) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException();
    }

    /**
     * This method lets a client join the game. It requires a username, that is from now on identifying the player
     * inside the game. Therefore, it must not be an already chosen username for this game.
     *
     * @param username the username of the player who wants to join
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      if the given username is null
     * @throws InvalidPhaseUpdateException   if a player with the same username is already taking part in this game
     * @throws InvalidPhaseUpdateException   if the maximum number of players has already joined the game
     */
    public Phase addPlayer(String username) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException();
    }

    /**
     * This method lets a player peacefully disconnect from the game lobby. From now on, the server will no longer keep
     * track of this player.
     *
     * @param username the username of the player
     * @throws UnsupportedOperationException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException      if the given username is null
     * @throws InvalidPhaseUpdateException   if the specified player is not taking part in this game
     */
    public Phase removePlayer(String username) throws InvalidPhaseUpdateException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns true if this Phase corresponds to a "game ended" scenario.
     *
     * @return true if this Phase corresponds to a "game ended" scenario
     */
    boolean isFinal() {
        return false;
    }

    /**
     * Returns a list with all the Players that meet the winner criteria. If the list is of length greater than 1, we
     * are in a tie situation. If the list is empty, that means that this phase isn't a final phase. To see the cases in
     * which a Player is a winner, check the game rules.
     *
     * @return a list with all the Players that meet the winner criteria
     * @see Phase#isFinal()
     */
    List<Player> getWinners() {
        return List.of();
    }
}
