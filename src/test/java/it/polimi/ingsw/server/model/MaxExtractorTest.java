package it.polimi.ingsw.server.model;

/**
 * Base class for testing MaxExtractor derivatives that contains shared logic between different classes.
 */
public class MaxExtractorTest {
    /**
     * Generates an array of Player objects
     */
    protected static Player[] genPlayerSet() {
        return new Player[]{
                new Player("Anna"),
                new Player("Bob"),
                new Player("Carl"),
                new Player("Denise")
        };
    }
}
