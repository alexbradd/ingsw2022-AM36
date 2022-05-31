package it.polimi.ingsw.server.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.controller.commands.UserCommandType;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.PhaseDiff;
import it.polimi.ingsw.server.net.Dispatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * Test class for the {@link MatchRegistry}.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class MatchRegistryTest {

    private final Dispatcher d1 = new Dispatcher(new Socket());
    private final Dispatcher d2 = new Dispatcher(new Socket());
    private final Dispatcher d3 = new Dispatcher(new Socket());

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

    /**
     * Test for the terminate() method.
     */
    @Test
    void terminateTest() {
        MatchRegistry.getInstance().create(0, 2, true);
        assertEquals(1, MatchRegistry.getInstance().getAll().size());

        MatchRegistry.getInstance().terminate(0);
        assertEquals(0, MatchRegistry.getInstance().getAll().size());

    }

    /**
     * Helper method for creating a CREATE command
     * @param name the name of the player
     * @param nPlayers the number of player of the Match
     * @param expert whether the Match should be in expert mode or not
     * @return the CREATE command
     */
    private JsonObject generateCreate(String name, int nPlayers, boolean expert) {
        JsonObject command = new JsonObject();
        command.addProperty("type", "CREATE");
        command.addProperty("username", name);

        JsonObject argument = new JsonObject();
        argument.addProperty("nPlayers", nPlayers);
        argument.addProperty("expert", expert);

        JsonArray arguments = new JsonArray();
        arguments.add(argument);

        command.add("arguments", arguments);

        return command;
    }

    /**
     * Helper method for creating a JOIN command
     * @param name the name of the player
     * @param id the id of the Match to join
     * @return the JOIN command
     */
    private JsonObject generateJoin(String name, int id) {
        JsonObject command = new JsonObject();
        command.addProperty("type", "JOIN");
        command.addProperty("username", name);
        command.addProperty("gameId", id);

        return command;
    }

    /**
     * Helper method for creating a LEAVE command
     * @param name the name of the player
     * @param id the id of the Match to leave
     * @return the LEAVE command
     */
    private JsonObject generateLeave(String name, int id) {
        JsonObject command = new JsonObject();
        command.addProperty("type", "LEAVE");
        command.addProperty("username", name);
        command.addProperty("gameId", id);

        return command;
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