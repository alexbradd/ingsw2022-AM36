package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.ProgramOptions;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.controller.commands.UserCommandType;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.PhaseDiff;
import it.polimi.ingsw.server.net.Dispatcher;
import org.junit.jupiter.api.*;

import java.io.File;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static it.polimi.ingsw.server.controller.ControllerTestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Test class for the {@link MatchRegistry}.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MatchRegistryTest {

    private final Dispatcher d1 = new Dispatcher(new Socket());
    private final Dispatcher d2 = new Dispatcher(new Socket());
    private final Dispatcher d3 = new Dispatcher(new Socket());

    @BeforeAll
    public void setUp() {
        new File("./target/eryantis-store").mkdirs();
        ProgramOptions.setPersistenceStore(new File("./target/eryantis-store"));
    }

    /**
     * Resets the singleton instance field before every test.
     */
    @BeforeEach
    public void resetSingleton() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field instance = MatchRegistry.class.getDeclaredField("registryInstance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    /**
     * Re-initializes the singleton instance with a custom match supplier (constructor of {@link SynchronousMatch})
     * before every test.
     */
    @BeforeEach
    void reInitialize() {
        MatchRegistry.getInstance(SynchronousMatch::new);
    }


    /**
     * Test for the creation of some Matches (and joining one).
     */
    @Test
    @DisplayName("Creating matches test")
    public void createMatchTest() {
        JsonObject c1 = generateCreate("alice", 2, true);
        JsonObject c2 = generateCreate("bob", 2, true);
        JsonObject j1 = generateJoin("carl", 0);
        MatchRegistry.getInstance().executeCommand(d1, c1);
        MatchRegistry.getInstance().executeCommand(d2, c2);
        MatchRegistry.getInstance().executeCommand(d3, j1);

        assertEquals(2, MatchRegistry.getInstance().getAll().size());
        assertEquals(2, MatchRegistry.getInstance().get(0).getDispatchers().size());
        assertEquals(1, MatchRegistry.getInstance().get(1).getDispatchers().size());

        assertIterableEquals(List.of(d1, d3),
                MatchRegistry.getInstance().get(0).getDispatchers());

        assertIterableEquals(List.of(d2),
                MatchRegistry.getInstance().get(1).getDispatchers());
    }

    /**
     * Test for joining/leaving matches.
     */
    @Test
    @DisplayName("Join/leave matches test")
    void joinLeaveTest() {
        JsonObject c1 = generateCreate("alice", 3, true);
        JsonObject j1 = generateJoin("bob", 0);
        JsonObject l1 = generateLeave("alice", 0);

        MatchRegistry.getInstance().executeCommand(d1, c1);
        MatchRegistry.getInstance().executeCommand(d2, j1);
        MatchRegistry.getInstance().executeCommand(d1, l1);

        assertEquals(1, MatchRegistry.getInstance().getAll().size());
        assertEquals(1, MatchRegistry.getInstance().get(0).getDispatchers().size());

        assertIterableEquals(List.of(d2),
                MatchRegistry.getInstance().get(0).getDispatchers());
    }
}

/**
 * Mock class for the {@link Match} class that executes all commands in the same (main) thread.
 */
class SynchronousMatch extends Match {

    public SynchronousMatch(int id, Game game) {
        super(id, game);
    }

    @Override
    public void executeUserCommand(UserCommand command, Dispatcher dispatcher) throws IllegalArgumentException {
        Game g = getGame();
        PhaseDiff diff;
        JsonObject res;

        try {
            diff = g.executeUserCommand(command);
            res = diff.toJson().getAsJsonObject();

        } catch (Exception exc) {
            res = Messages.buildErrorMessage(exc.getMessage());
            dispatcher.send(res);
        }

        UserCommandType type = command.getType();
        String username = command.getUsername();

        if (type.equals(UserCommandType.JOIN))
            addDispatcher(dispatcher, username);
        if (type.equals(UserCommandType.LEAVE))
            removeDispatcher(dispatcher, username);

        for (Dispatcher d : new ArrayList<>(getDispatchers()))
            d.send(res);
    }
}