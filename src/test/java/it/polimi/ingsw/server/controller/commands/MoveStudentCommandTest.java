package it.polimi.ingsw.server.controller.commands;

import com.google.gson.*;
import it.polimi.ingsw.enums.PieceColor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * In depth testing of {@link MoveStudentCommand}'s argument parsing
 */
class MoveStudentCommandTest {
    private JsonObject base;

    /**
     * Sets up common attributes.
     */
    @BeforeEach
    void setup() {
        base = new JsonObject();
        base.addProperty("gameId", 0);
        base.addProperty("type", "MOVE_STUDENT");
        base.addProperty("username", "ann");
    }

    /**
     * Checks that a MoveStudentCommand is correctly created when passed a correctly formatted MoveParameter with an
     * HALL destination
     */
    @Test
    void succeeds_withCorrectHall() {
        MoveStudentCommand.MoveParameter p = new MoveStudentCommand.MoveParameter();
        p.setColor(PieceColor.BLUE);
        p.setDestination(MoveStudentCommand.MoveDestination.HALL);
        JsonArray arr = new JsonArray();
        arr.add(new Gson().toJsonTree(p));
        base.add("arguments", arr);

        MoveStudentCommand cmd = new MoveStudentCommand(base);
        assertEquals(UserCommandType.MOVE_STUDENT, cmd.getType());
        assertEquals(0L, cmd.getGameId());
        assertEquals("ann", cmd.getUsername());
        assertEquals(p.getDestination(), cmd.getArg().getDestination());
        assertEquals(p.getColor(), cmd.getArg().getColor());
    }

    /**
     * Checks that a MoveStudentCommand is correctly created when passed a correctly formatted MoveParameter with an
     * ISLAND destination
     */
    @Test
    void succeeds_withCorrectIsland() {
        MoveStudentCommand.MoveParameter p = new MoveStudentCommand.MoveParameter();
        p.setColor(PieceColor.BLUE);
        p.setDestination(MoveStudentCommand.MoveDestination.ISLAND);
        p.setIndex(0);
        JsonArray arr = new JsonArray();
        arr.add(new Gson().toJsonTree(p));
        base.add("arguments", arr);

        MoveStudentCommand cmd = new MoveStudentCommand(base);
        assertEquals(UserCommandType.MOVE_STUDENT, cmd.getType());
        assertEquals(0L, cmd.getGameId());
        assertEquals("ann", cmd.getUsername());
        assertEquals(p.getDestination(), cmd.getArg().getDestination());
        assertEquals(p.getColor(), cmd.getArg().getColor());
        assertEquals(p.getIndex(), cmd.getArg().getIndex());
    }

    /**
     * Checks that a MovesStudentCommand is correctly created when passed an argument Object with HALL destination and
     * an index, even illegal.
     */
    @Test
    void succeeds_ignoresIndexIfHall() {
        JsonArray arr = new JsonArray();
        arr.add(
                JsonParser.parseString("{`destination`:`HALL`,`color`:`RED`,`index`:-5}".replace('`', '"'))
        );
        base.add("arguments", arr);

        MoveStudentCommand cmd = new MoveStudentCommand(base);
        assertEquals(MoveStudentCommand.MoveDestination.HALL, cmd.getArg().getDestination());
        assertEquals(PieceColor.RED, cmd.getArg().getColor());
        assertEquals(-5, cmd.getArg().getIndex());
    }

    /**
     * Checks that {@link SingleArgumentCommand#getModificationMessage()} does not return null or an empty string.
     */
    @Test
    void succeeds_withValidObject_modificationMessageValidString() {
        MoveStudentCommand.MoveParameter p = new MoveStudentCommand.MoveParameter();
        p.setColor(PieceColor.BLUE);
        p.setDestination(MoveStudentCommand.MoveDestination.HALL);
        JsonArray arr = new JsonArray();
        arr.add(new Gson().toJsonTree(p));
        base.add("arguments", arr);

        MoveStudentCommand cmd = new MoveStudentCommand(base);

        String msg = cmd.getModificationMessage();
        assertNotNull(msg);
        assertNotEquals("", msg);
    }

    /**
     * Checks that a MoveStudentCommand is not created when the invalid argument object passed as parameter is used.
     */
    @ParameterizedTest
    @MethodSource("invalidObjectSource")
    void fails_withInvalidObject(JsonElement argObj) {
        JsonArray arr = new JsonArray();
        arr.add(argObj);
        base.add("arguments", arr);

        assertThrows(IllegalArgumentException.class, () -> new MoveStudentCommand(base));
    }

    /**
     * Generates illegal argument objects to be fed to MoveStudentCommand's constructor. In order, the cases checked
     * are:
     *
     * <ol>
     *     <li>JsonNull</li>
     *     <li>empty object</li>
     *     <li>object without destination</li>
     *     <li>object with illegal destination</li>
     *     <li>object with non-string destination</li>
     *     <li>with null destination</li>
     *     <li>object with no color</li>
     *     <li>object with illegal color</li>
     *     <li>object with null color</li>
     *     <li>object with non-string color</li>
     *     <li>ISLAND move without index</li>
     *     <li>ISLAND move with negative index</li>
     *     <li>ISLAND move with non-number index</li>
     * </ol>
     */
    static Stream<JsonElement> invalidObjectSource() {
        return Stream.of(
                JsonNull.INSTANCE,
                JsonParser.parseString("{}"),
                JsonParser.parseString("{`color`:`BLUE`,`index`:2}".replace('`', '"')),
                JsonParser.parseString("{`destination`:`sium`,`color`:`BLUE`,`index`:2}".replace('`', '"')),
                JsonParser.parseString("{`destination`:1,`color`:`BLUE`,`index`:2}".replace('`', '"')),
                JsonParser.parseString("{`destination`:null,`color`:`BLUE`,`index`:2}".replace('`', '"')),
                JsonParser.parseString("{`destination`:`ISLAND`,`index`:2}".replace('`', '"')),
                JsonParser.parseString("{`destination`:`ISLAND`,`color`:`sium`,`index`:2}".replace('`', '"')),
                JsonParser.parseString("{`destination`:`ISLAND`,`color`:null,`index`:2}".replace('`', '"')),
                JsonParser.parseString("{`destination`:`ISLAND`,`color`:1,`index`:2}".replace('`', '"')),
                JsonParser.parseString("{`destination`:`ISLAND`,`color`:`RED`}".replace('`', '"')),
                JsonParser.parseString("{`destination`:`ISLAND`,`color`:`RED`,`index`:-10}".replace('`', '"')),
                JsonParser.parseString("{`destination`:`ISLAND`,`color`:`RED`,`index`:`sium`}".replace('`', '"'))
        );
    }
}