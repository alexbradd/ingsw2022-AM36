package it.polimi.ingsw.server.model;

/**
 * Simple java bean containing various tunables about a game of Eriantys
 */
public class GameParameters {
    private final int nPlayers;
    private final int nOfCharacters = 3;
    private final int nOfProfessors = 5;
    private final int nStudentsOfColor = 26;
    private final int nIslands = 12;
    private final int nStudentsEntrance;
    private final int nStudentsMovable;
    private final int nTowers;
    private final boolean expertMode;

    /**
     * The number of students of each color to place on the Islands during initialization.
     */
    private final int nStudentsInSack = 2;

    private GameParameters(int nPlayers, int nStudentsEntrance, int nStudentsMovable, int nTowers, boolean expertMode) {
        this.nPlayers = nPlayers;
        this.nStudentsEntrance = nStudentsEntrance;
        this.nStudentsMovable = nStudentsMovable;
        this.nTowers = nTowers;
        this.expertMode = expertMode;
    }

    /**
     * Return the number of Players
     *
     * @return the number of Players
     */
    public int getnPlayers() {
        return nPlayers;
    }

    /**
     * Return the number of Character cards
     *
     * @return the number of Character cards
     */
    public int getnOfCharacters() {
        return nOfCharacters;
    }

    /**
     * Returns the number of Professors
     *
     * @return the number of Professors
     */
    public int getnOfProfessors() {
        return nOfProfessors;
    }

    /**
     * Returns the number of Students for each color
     *
     * @return the number of Students for each color
     */
    public int getnStudentsOfColor() {
        return nStudentsOfColor;
    }

    /**
     * Returns the maximum number of Students each player's entrance should hold
     *
     * @return the maximum number of Students each player's entrance should hold
     */
    public int getnStudentsEntrance() {
        return nStudentsEntrance;
    }

    /**
     * Returns the number of Students movable during the {@link StudentMovePhase}
     *
     * @return the number of Students movable during the {@link StudentMovePhase}
     */
    public int getnStudentsMovable() {
        return nStudentsMovable;
    }

    /**
     * Returns the number of Towers each player should have at the beginning of the game.
     *
     * @return the number of Towers each player should have at the beginning of the game
     */
    public int getnTowers() {
        return nTowers;
    }

    public int getnIslands() { return nIslands; }

    /**
     * Return true if this match is being played in expertMode
     *
     * @return true if this match is being played in expertMode
     */
    public boolean isExpertMode() {
        return expertMode;
    }

    public int getnStudentsInSack() {
        return nStudentsInSack;
    }

    /**
     * Returns a preconfigured GameParameters for a two player game.
     *
     * @param expertMode flag for indicating whether this game will be played with expert rules or not
     * @return a preconfigured GameParameters
     */
    public static GameParameters twoPlayerGame(boolean expertMode) {
        return new GameParameters(2, 7, 3, 8, expertMode);
    }

    /**
     * Returns a preconfigured GameParameters for a three player game.
     *
     * @param expertMode flag for indicating whether this game will be played with expert rules or not
     * @return a preconfigured GameParameters
     */
    public static GameParameters threePlayersGame(boolean expertMode) {
        return new GameParameters(3, 9, 4, 6, expertMode);
    }
}