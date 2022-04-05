package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;

/**
 * Base class for testing MaxExtractor derivatives that contains shared logic between different classes.
 */
public class MaxExtractorTest {
    /**
     * Generates an array of Player objects
     */
    protected static Player[] genPlayerSet() {
        return new Player[]{
                new Player("Anna", 1, 1, TowerColor.WHITE),
                new Player("Bob", 1, 1, TowerColor.WHITE),
                new Player("Carl", 1, 1, TowerColor.WHITE),
                new Player("Denise", 1, 1, TowerColor.WHITE)
        };
    }
}
