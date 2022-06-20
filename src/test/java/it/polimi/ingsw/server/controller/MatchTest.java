package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.net.Dispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link Match}.
 */
class MatchTest {

    Dispatcher d1 = new Dispatcher(new Socket()),
            d2 = new Dispatcher(new Socket()),
            d3 = new Dispatcher(new Socket());
    Match m;

    /**
     * Tests for constructor's exception throwing and creates a new Match.
     */
    @Test
    @DisplayName("Constructor test")
    @BeforeEach
    void constructorTest() {
        m = new Match(0, new Game(2, true));

        assertThrows(IndexOutOfBoundsException.class,
                () -> new Match(-1, new Game(2, true)));

        assertThrows(IllegalArgumentException.class,
                () -> new Match(0, (Game) null));
    }

    /**
     * Tests the adding and removal of dispatchers from the Match (and exception throwing).
     */
    @Test
    @DisplayName("Adding and removal of dispatcher")
    void dispatchersTest() {
        assertAll("adding dispatchers",
                () -> m.addDispatcher(d1, "alice"),
                () -> m.addDispatcher(d2, "bob"));

        assertIterableEquals(List.of(d1, d2), m.getDispatchers());
        assertThrows(IllegalArgumentException.class, () -> m.addDispatcher(d1, "carl"));

        assertThrows(IllegalArgumentException.class, () -> m.addDispatcher(d1, null));
        assertThrows(IllegalArgumentException.class, () -> m.addDispatcher(d1, ""));
        assertThrows(IllegalArgumentException.class, () -> m.addDispatcher(d1, "°~°"));


        assertThrows(IllegalArgumentException.class, () -> m.removeDispatcher(null, "alice"));
        assertThrows(IllegalArgumentException.class, () -> m.removeDispatcher(d1, null));
        assertThrows(IllegalArgumentException.class, () -> m.removeDispatcher(d1, "trudy"));
        assertThrows(IllegalArgumentException.class, () -> m.removeDispatcher(d3, "a"));


        assertAll("removing dispatchers",
                () -> m.removeDispatcher(d1, "alice"),
                () -> m.removeDispatcher(d2, "bob"));

        assertIterableEquals(new ArrayList<>(), m.getDispatchers());
        assertThrows(IllegalArgumentException.class, () -> m.removeDispatcher(d2, "bob"));
    }
}