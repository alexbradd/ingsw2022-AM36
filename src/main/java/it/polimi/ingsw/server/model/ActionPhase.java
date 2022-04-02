package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;
import java.security.InvalidParameterException;

/**
 * This phase represents a state of the game in which a player is currently playing. Its subclasses represent the
 * sub-phases in which a player's action turn can be subdivided. The ActionPhase class defines the common operations
 * between them.
 *
 * @author Leonardo Bianconi
 * @see Phase
 * @see PreparePhase
 * @see StudentMovePhase
 * @see MNMovePhase
 * @see CloudPickPhase
 * @see EndgamePhase
 */

abstract class ActionPhase extends Phase {
    /**
     * An iterator that specifies the order of next players to play.
     */
    protected PlayerListIterator iterator;
    /**
     * The player currently playing.
     */
    protected Player curPlayer;
    /**
     * Whether influence needs to be calculated on another island besides the one Mother Nature steps on (character effect
     * - see game rules).
     */
    protected boolean extraInfluenceCalculation;
    /**
     * The {@link MaxExtractor} instance for the calculations of professor acquisition during the round (may vary based on a
     * character effect - see game rules).
     */
    protected MaxExtractor maximumExtractor;
    /**
     * The extra number of steps that Mother Nature can take during this round (the default value is 0, may vary based on
     * a character effect - see game rules).
     */
    protected int extraMNMoves;


    /**
     * Additional constructor, selects automatically the first player to play, based on previously played assistants.
     *
     * @param game the Game instance
     */
    protected ActionPhase(Game game) {
        super(game);
        extraInfluenceCalculation = false;
        maximumExtractor = new EqualityExclusiveMaxExtractor();
        extraMNMoves = 0;
    }

    /**
     * The base constructor.
     *
     * @param game the Game instance
     */
    protected ActionPhase(Game game, PlayerListIterator iterator, Player player) {
        super(game);
    }

    /**
     * It returns the Sack object instance.
     *
     * @return the {@link Sack} instance
     */
    protected Sack getSack() {
        return null;
    }

    /**
     * It returns the PlayerListIterator instance, corresponding to the next players to play, in the correct
     * order.
     *
     * @return the {@link PlayerListIterator} instance
     */
    protected PlayerListIterator getPlayerListIterator() {
        return null;
    }

    /**
     * It returns the Player instance corresponding to the currently playing player.
     *
     * @return the {@link Player} currently playing
     */
    protected Player getCurrentPlayer() {
        return null;
    }

    /**
     * This method recalculates the affiliation of professors to players' schools. The calculation algorithm varies depending
     * on the maximumExtractor attribute.
     */
    protected void assignProfessors() {
    }

    /**
     * This method swaps two students between two locations on the table.
     * It moves a student of the "colorSource" color from the "source" to the "destination", and a student of
     * the "colorDestination" color from the "destination" to the "source". Because it is a swap and not a single
     * movement, both source and destination objects need to implement the {@link BidirectionalStudentMove} interface.
     *
     * @param colorSource      the color of the student in the source instance
     * @param colorDestination the color of the student in the destination instance
     * @param source           the source instance
     * @param destination      the destination instance
     */
    protected void swapStudents(PieceColor colorSource, PieceColor colorDestination, BidirectionalStudentMove source, BidirectionalStudentMove destination) {
    }

    /**
     * This method sets the influence calculator.
     *
     * @param calculator the instance that implements the {@link InfluenceCalculator} interface
     */
    protected void setInfluenceCalculator(InfluenceCalculator calculator) {
    }

    /**
     * This method sets the way of assigning professors to players (that is, a {@link MaxExtractor} instance)
     *
     * @param maxExtractor the {@link MaxExtractor} instance
     */
    protected void setMaxExtractor(MaxExtractor maxExtractor) {
    }

    /**
     * This method requests an extra influence calculation to be done in another island besides the one Mother Nature steps
     * on (see game rules).
     *
     * @param islandPiece the island to be calculated on
     */
    protected void requestExtraInfluenceCalculation(IslandPiece islandPiece) {
    }

    /**
     * This method requests an extension of Mother Nature movement by a certain amount of steps.
     *
     * @param steps the amount of steps of the extension
     */
    protected void requestMNMovementExtension(int steps) {
    }

    /**
     * It checks all win conditions, returning whether there is a winner for the game or not.
     *
     * @return whether one (or more) player has won the game
     */
    public boolean checkWin() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Island getIsland(int id) throws OperationNotSupportedException, IndexOutOfBoundsException {
        return super.getIsland(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveStudent(PieceColor color, StudentMoveSource source, StudentMoveDestination destination) throws OperationNotSupportedException, NullPointerException, IllegalArgumentException {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playCharacter(String username, Character character, int... args) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, InvalidParameterException, NotEnoughCoinsException {
        super.playCharacter(username, character, args);
    }
}
