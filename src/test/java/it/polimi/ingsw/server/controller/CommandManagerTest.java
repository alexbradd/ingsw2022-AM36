package it.polimi.ingsw.server.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.server.controller.commands.JoinCommand;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.net.Dispatcher;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the {@link CommandManager} class.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommandManagerTest {
    private Game g;
    private Match m;
    private Dispatcher d1, d2, d3;
    private UserCommand c1, c2, c3;

    /**
     * Sets up private variables (three JOIN commands coming from three different Dispatchers and the Match instance).
     */
    @BeforeAll
    void setup() {

        // build commands
        String str1 = "{ \"type\": \"JOIN\", \"username\": \"alice\", \"gameId\": 0 }";
        JsonObject json1 = new Gson().fromJson(str1, JsonObject.class);
        c1 = new JoinCommand(json1);

        String str2 = "{ \"type\": \"JOIN\", \"username\": \"bob\", \"gameId\": 0 }";
        JsonObject json2 = new Gson().fromJson(str2, JsonObject.class);
        c2 = new JoinCommand(json2);

        String str3 = "{ \"type\": \"JOIN\", \"username\": \"carl\", \"gameId\": 0 }";
        JsonObject json3 = new Gson().fromJson(str3, JsonObject.class);
        c3 = new JoinCommand(json3);

        g = new Game(3, true);
        m = new Match(0, g);
        d1 = new Dispatcher(new Socket());
        d2 = new Dispatcher(new Socket());
        d3 = new Dispatcher(new Socket());
    }

    /**
     * Tests for the execution of the three JOIN commands and the emptying of the command queue.
     */
    @Test
    @DisplayName("Commands execution test")
    void commandsExecutionTest() {
        m.executeUserCommand(c1, d1);
        m.executeUserCommand(c2, d2);
        m.executeUserCommand(c3, d3);

        assertEquals(3, m.getCommands().size());

        new SynchCommandManager(m).run();

        assertEquals(0, m.getCommands().size());
    }

}

/**
 * Mock class for the {@link CommandManager} class. Instead of using the blocking method {@link BlockingQueue#take()},
 * it uses the {@link BlockingQueue#remove()} method.
 */
class SynchCommandManager extends CommandManager {

    public SynchCommandManager(Match match) {
        super(match);
    }

    @Override
    public void run() {
        while (!getMatch().getCommands().isEmpty())
            manageCommand(getMatch().getCommands().remove());
    }
}