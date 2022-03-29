package it.polimi.ingsw.server.model;

import javax.naming.OperationNotSupportedException;
import java.security.InvalidParameterException;
import java.util.concurrent.ExecutionException;

abstract class ActionPhase extends Phase {
    private PlayerListIterator iterator;
    private Player curPlayer;
    private boolean extraInfluenceCalculator;
    private MaxExtractor maximumExtractor;
    private int extraMNMoves;


    /**
     * {@inheritDoc}
     */
    protected ActionPhase(Game game) {
        super(game);
    }

    /**
     * The base constructor. // TODO
     *
     * @param game
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
     * @return
     */
    protected Player getCurrentPlayer() {
        return null;
    }

    /**
     *
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
     * // TODO
     *
     * @param islandPiece
     */
    protected void requestExtraInfluenceCalculator(IslandPiece islandPiece) {
    }

    /**
     * // TODO
     *
     * @param steps
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
        super.moveStudent(color, source, destination);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playCharacter(String username, Character character, int... args) throws OperationNotSupportedException, NullPointerException, InvalidPlayerException, InvalidParameterException, NotEnoughCoinsException {
        super.playCharacter(username, character, args);
    }
}
