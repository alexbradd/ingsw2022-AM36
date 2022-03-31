package it.polimi.ingsw.server.model;

import java.util.ArrayList;

/**
 * The GameFactory class is the only responsible for the creation of new Game instances. Its methods (factory methods)
 * create the correct Game instance based on the parameters given.
 *
 * @author Leonardo Bianconi
 * @see Game
 */
public class GameFactory {
    /**
     * It creates a new instance of a two player {@link Game}, setting the correct game parameters for a game of this
     * type (see game rules)
     *
     * @param expertMode whether to create a game with expert rules (see game rules)
     * @return the {@link Game} instance with correctly set parameters
     */
    public static Game twoPlayerGame(boolean expertMode) {
        Game newGame = new Game(2,
                8,
                3,
                7,
                expertMode,
                new Sack(),
                new ArrayList<Cloud>(),
                new IslandList(),
                new MotherNature(),
                new Professor[5],
                expertMode ? CharacterFactory.pickThreeRandom() : null,
                new PlayerList(),
                false
        );
        return newGame;
    }

    /**
     * It creates a new instance of a three player {@link Game}, setting the correct game parameters for a game of this
     * type (see game rules)
     *
     * @param expertMode whether to create a game with expert rules (see game rules)
     * @return the {@link Game} instance with correctly set parameters
     */
    public static Game threePlayerGame(boolean expertMode) {
        Game newGame = new Game(3,
                6,
                4,
                9,
                expertMode,
                new Sack(),
                new ArrayList<Cloud>(),
                new IslandList(),
                new MotherNature(),
                new Professor[5],
                expertMode ? CharacterFactory.pickThreeRandom() : null,
                new PlayerList(),
                false
        );
        return newGame;
    }
}
