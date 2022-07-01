package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.controller.commands.Parser;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;
import it.polimi.ingsw.server.model.exceptions.InvalidPlayerException;
import it.polimi.ingsw.server.net.Dispatcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the {@link CommandManager} class.
 */
class CommandManagerTest {

    /**
     * Tests that if a {@link Match} is not in rejoining state, the {@link CommandManager} for that Match will choose to
     * execute that command via a {@link StandardCommandStrategy}.
     */
    @Test
    @DisplayName("Standard strategy test")
    void commandExecutionTest() throws NoSuchFieldException, IllegalAccessException {
        Game g = new Game(2, false);
        Match m = new Match(999, g);
        CommandManager c = new CommandManager(m);

        JsonObject joinCommand = ControllerTestUtils.generateJoin("alice", 999);
        c.manageCommand(new Tuple<>(Parser.parse(joinCommand), new Dispatcher(new Socket())));

        Field strategyField = c.getClass().getDeclaredField("strategy");
        strategyField.setAccessible(true);
        CommandStrategy strategy = (CommandStrategy) strategyField.get(c);

        assertEquals(StandardCommandStrategy.class, strategy.getClass());
    }

    /**
     * Tests that if a {@link Match} is in rejoining state, the {@link CommandManager} for that Match will choose to
     * execute that command via a {@link RejoiningCommandStrategy}.
     */
    @Test
    @DisplayName("Rejoining strategy test")
    void commandExecutionInRejoiningTest() throws InvalidPlayerException, InvalidCharacterParameterException, InvalidPhaseUpdateException, NoSuchFieldException, IllegalAccessException {
        Game g = new Game(2, false);
        Match m = new Match(999, g);
        CommandManager c = new CommandManager(m);

        g.executeUserCommand(Parser.parse(ControllerTestUtils.generateJoin("alice", 999)));

        JsonObject joinCommand = ControllerTestUtils.generateLeave("bob", 999);
        c.manageCommand(new Tuple<>(Parser.parse(joinCommand), new Dispatcher(new Socket())));

        Field strategyField = c.getClass().getDeclaredField("strategy");
        strategyField.setAccessible(true);
        CommandStrategy strategy = (CommandStrategy) strategyField.get(c);

        assertEquals(RejoiningCommandStrategy.class, strategy.getClass());
    }

    /**
     * Deletes the created persistence files.
     */
    @AfterAll
    static void terminateMatches() {
        MatchRegistry.getInstance().getPersistenceManager().drop(999);
    }
}