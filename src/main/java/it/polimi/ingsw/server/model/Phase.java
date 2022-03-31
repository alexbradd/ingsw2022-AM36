package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;
import java.security.InvalidParameterException;

/**
 * The Phase class represents a single state of the game, it is linked to the Game instance, and it is a facade facing
 * the controller via the Command pattern, showing the available operations (its methods), that interact with the
 * internal state of all model entities.
 *
 * @author Leonardo Bianconi
 * @see Game
 * @see LobbyPhase
 * @see PreparePhase
 * @see PlanningPhase
 * @see ActionPhase
 * @see EndgamePhase
 */

abstract public class Phase {
    /**
     * A reference to the {@link Game} instance.
     */
    private Game game;

    /**
     * The default constructor.
     *
     * @param game the {@link Game} instance.
     */
    protected Phase(Game game) {
        this.game = game;
    }

    /**
     * The main method of the phase, keeps waiting for the correct function calls, i.e. the ones that make the game evolve to
     * new phases.
     *
     * @return the next phase of the game.
     */
    abstract public Phase doPhase();

    /**
     * It returns the {@link Character} object given its in-game id.
     *
     * @param id the in-game id of the character (i.e. a integer lower than the maximum number of characters on the table)
     * @return the {@link Character} instance associated with the passed id
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws IndexOutOfBoundsException      if the id passed is out of bounds
     */
    public Character getCharacter(int id) throws OperationNotSupportedException, IndexOutOfBoundsException {
        throw new OperationNotSupportedException();
    }

    /**
     * It returns the {@link Island} object given its id.
     *
     * @param id the id associated with the island
     * @return the {@link Island} instance with the passed id
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws IndexOutOfBoundsException      if the given id is out of bounds
     */
    public Island getIsland(int id) throws OperationNotSupportedException, IndexOutOfBoundsException {
        throw new OperationNotSupportedException();
    }

    /**
     * This method lets the player identified by the given username choose the mage deck corresponding to the given id.
     *
     * @param username the username of the player
     * @param id       the id associated with the mage
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws IndexOutOfBoundsException      if the given id is out of bounds
     * @throws NullPointerException           if the given username is null
     * @throws InvalidPlayerException         either if the specified player username is invalid or it is not the specified
     *                                        player's turn
     * @throws MageAlreadyChosenException     if the mage has already been chosen by another player
     */

    public void chooseMageDeck(String username, int id) throws OperationNotSupportedException, IndexOutOfBoundsException, NullPointerException, InvalidPlayerException, MageAlreadyChosenException {
        throw new OperationNotSupportedException();
    }

    /**
     * This method lets the player identified by the given username play the assistant card of his deck corresponding
     * to the id passed to the method.
     *
     * @param username the username of the player
     * @param id       the id associated with the assistant
     * @throws OperationNotSupportedException  if this operation is not supported by the current phase of the game
     * @throws NullPointerException            if the given username is null
     * @throws InvalidPlayerException          either if the specified player username is invalid or it is not specified
     *                                         player's turn
     * @throws AssistantAlreadyPlayedException if this assistant has already been played by another player in this round
     * @throws AssistantNotInDeckException     if this assistant has already been played by the player
     * @throws IndexOutOfBoundsException       if the given id is out of bounds
     */

    public void playAssistant(String username, int id) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, AssistantAlreadyPlayedException, AssistantNotInDeckException, IndexOutOfBoundsException {
        throw new OperationNotSupportedException();
    }

    /**
     * This method returns the {@link Entrance} instance of the player associated with the given username.
     *
     * @param username the username of the player
     * @return the {@link Entrance} instance of the player
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws NullPointerException           if the given username is null
     * @throws InvalidPlayerException         if the specified player username is invalid
     */
    public Entrance getPlayerEntrance(String username) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException {
        throw new OperationNotSupportedException();
    }

    /**
     * This method returns the {@link Hall} instance of the player associated with the given username.
     *
     * @param username the player's username
     * @return the {@link Hall} instance of the player
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws NullPointerException           if the given username is null
     * @throws InvalidPlayerException         if the specified player username is invalid
     */
    public Hall getPlayerHall(String username) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException {
        throw new OperationNotSupportedException();
    }

    /**
     * This method performs a move of a single student, identified by its color, from a source (an object that implements the
     * {@link StudentMoveSource} interface) to a destination (that implements the {@link StudentMoveDestination} interface).
     * It is used for all student movements across the playing area.
     *
     * @param color       the student piece color
     * @param source      the source of the student movement
     * @param destination the destination of the student movement
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws IllegalArgumentException       if the movement is not allowed
     * @throws NullPointerException           if any of the parameters is null
     */
    public void moveStudent(PieceColor color, StudentMoveSource source, StudentMoveDestination destination) throws OperationNotSupportedException, IllegalArgumentException, NullPointerException {
        throw new OperationNotSupportedException();
    }

    /**
     * This method lets the player identified by its username perform a movement of Mother Nature across the islands, for
     * a total number of steps specified via parameter. The movement is performed clockwise on the islands (see game rules).
     *
     * @param username the username of the player
     * @param steps    the number of steps of the movement
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws NullPointerException           if the given username is null
     * @throws InvalidPlayerException         either if the specified player username is invalid or it is not the specified
     *                                        player's turn
     * @throws IllegalArgumentException       if the number of steps is invalid (see game rules)
     */
    public void moveMN(String username, int steps) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, IllegalArgumentException {
        throw new OperationNotSupportedException();
    }

    /**
     * This method lets the player identified by his username play a character card. The {@link Character} instance is
     * passed via parameter, along with all the needed additional information, as can be seen below:
     * <br>
     *
     * @param username  the username of the player
     * @param character the {@link Character} instance
     * @param args      additional arguments that specify the behaviour of the card (see method description)
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws NullPointerException           if any of the parameters is null
     * @throws InvalidPlayerException         either if the specified player username is invalid or it is not the specified
     *                                        player's turn
     * @throws IllegalArgumentException       if the additional information "args" is either missing or incorrect
     * @throws NotEnoughCoinsException        if the number of coins of the player is less than the amount required to play the
     *                                        character card (see game rules)
     */

    public void playCharacter(String username, Character character, int... args) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, IllegalArgumentException, NotEnoughCoinsException {
        throw new OperationNotSupportedException();
    }

    /**
     * This method lets the player identified by his username pick a cloud from the playing area and retrieve all the
     * students placed on it (see game rules).
     *
     * @param username the username of the player
     * @param id       the id of the cloud
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws NullPointerException           if the given username is null
     * @throws InvalidPlayerException         either if the specified player username is invalid or it is not specified
     *                                        player's turn
     * @throws IndexOutOfBoundsException      if the id does not represent a valid cloud
     * @throws CloudAlreadyChosenException    if the specified cloud has already been picked this round
     */
    public void pickCloud(String username, int id) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, IndexOutOfBoundsException, CloudAlreadyChosenException {
        throw new OperationNotSupportedException();
    }

    /**
     * This method lets a client join the game. It requires a username, that is from now on identifying the player inside
     * the game. Therefore, it must not be an already chosen username for this game.
     *
     * @param username the username of the player who wants to join
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws NullPointerException           if the given username is null
     * @throws PlayerAlreadyInGameException   if a player with the same username is already taking part in this game
     */
    public void addPlayer(String username) throws OperationNotSupportedException, NullPointerException, PlayerAlreadyInGameException {
        throw new OperationNotSupportedException();
    }

    /**
     * This method lets a player peacefully disconnect from the game lobby. From now on, the server will no longer keep
     * track of this player.
     *
     * @param username the username of the player
     * @throws OperationNotSupportedException if this operation is not supported by the current phase of the game
     * @throws NullPointerException           if the given username is null
     * @throws PlayerNotInGameException       if the specified player is not taking part in this game
     */
    public void removePlayer(String username) throws OperationNotSupportedException, NullPointerException, PlayerNotInGameException {
        throw new OperationNotSupportedException();
    }
}