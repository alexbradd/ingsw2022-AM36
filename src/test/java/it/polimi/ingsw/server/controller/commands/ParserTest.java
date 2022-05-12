package it.polimi.ingsw.server.controller.commands;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class {@link Parser}. It checks only for a small subset of possibilities since most of the parsing is done
 * inside the commands themselves, and therefore tested in the respective test classes
 *
 * @see CommonCommandParsingTest
 * @see SingleArgumentCommandTest
 * @see PlayCharacterCommandTest
 * @see MoveStudentCommand
 */
class ParserTest {
    /**
     * Checks that an exception is thrown if null is passed
     */
    @Test
    void withNull() {
        assertThrows(IllegalArgumentException.class, () -> Parser.parse(null));
    }

    /**
     * Checks that an exception is thrown if an object without a type is passed
     */
    @Test
    void withoutType() {
        JsonObject object = new JsonObject();
        assertThrows(IllegalArgumentException.class, () -> Parser.parse(object));
    }

    /**
     * Checks that an exception is thrown if an object with a non-string type is passed
     */
    @Test
    void withNonStringType() {
        JsonObject object = new JsonObject();
        object.addProperty("type", 1);
        assertThrows(IllegalArgumentException.class, () -> Parser.parse(object));
    }

    /**
     * Checks that an exception is thrown if an object with an invalid type string is passed
     */
    @Test
    void withIllegalType() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "SIUM");
        assertThrows(IllegalArgumentException.class, () -> Parser.parse(object));
    }

    /**
     * Checks that non-null UserCommand is created if a correctly formatted object is passed
     */
    @Test
    void withCorrectObject() {
        JsonObject object = new JsonObject();
        object.addProperty("type", "JOIN");
        object.addProperty("gameId", 0);
        object.addProperty("username", "ann");

        UserCommand cmd = Parser.parse(object);
        assertNotNull(cmd);
    }
}