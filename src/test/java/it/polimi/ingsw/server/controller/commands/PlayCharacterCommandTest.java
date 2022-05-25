package it.polimi.ingsw.server.controller.commands;

import com.google.gson.*;
import it.polimi.ingsw.server.model.CharacterStep;
import it.polimi.ingsw.enums.CharacterType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * In depth testing of {@link PlayCharacterCommand}'s argument parsing.
 */
class PlayCharacterCommandTest {
    JsonObject base;

    /**
     * Sets up common attributes.
     */
    @BeforeEach
    void setup() {
        base = new JsonObject();
        base.addProperty("gameId", 0);
        base.addProperty("type", "PLAY_CHARACTER");
        base.addProperty("username", "ann");
    }

    /**
     * Check that CharacterInvocation correctly converts maps to CharacterStep
     */
    @Test
    void invocation_characterStepConversion() {
        PlayCharacterCommand.CharacterInvocation invocation = new PlayCharacterCommand.CharacterInvocation();
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");
        invocation.setSteps(List.of(map));

        CharacterStep[] arr = invocation.getStepsAsCharacterStep();
        assertEquals(1, arr.length);
        assertEquals("value", arr[0].getParameter("key"));
    }

    /**
     * Checks that a PlayCharacterCommand is correctly created when passed a correctly formatted CharacterInvocation
     */
    @Test
    void succeeds_withValidObject() {
        PlayCharacterCommand.CharacterInvocation invocation = new PlayCharacterCommand.CharacterInvocation();
        invocation.setCharacter(CharacterType.CENTAUR);
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");
        invocation.setSteps(List.of(map));

        JsonArray args = new JsonArray();
        JsonElement j = new Gson().toJsonTree(invocation);
        args.add(j);
        base.add("arguments", args);

        PlayCharacterCommand cmd = new PlayCharacterCommand(base);
        assertEquals(UserCommandType.PLAY_CHARACTER, cmd.getType());
        assertEquals(0L, cmd.getGameId());
        assertEquals("ann", cmd.getUsername());
        assertEquals(invocation.getCharacter(), cmd.getArg().getCharacter());
        assertEquals(invocation.getSteps(), cmd.getArg().getSteps());
    }

    /**
     * Checks that {@link SingleArgumentCommand#getModificationMessage()} does not return null or an empty string.
     */
    @Test
    void succeeds_withValidObject_modificationMessageValidString() {
        PlayCharacterCommand.CharacterInvocation invocation = new PlayCharacterCommand.CharacterInvocation();
        invocation.setCharacter(CharacterType.CENTAUR);
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "value");
        invocation.setSteps(List.of(map));

        JsonArray args = new JsonArray();
        JsonElement j = new Gson().toJsonTree(invocation);
        args.add(j);
        base.add("arguments", args);

        PlayCharacterCommand cmd = new PlayCharacterCommand(base);

        String msg = cmd.getModificationMessage();
        assertNotNull(msg);
        assertNotEquals("", msg);
    }

    /**
     * Checks that a PlayCharacterCommand is not created when the invalid argument object passed as parameter is used.
     */
    @ParameterizedTest
    @MethodSource("invalidObjectSource")
    void fails_withInvalidObject(JsonElement argObj) {
        JsonArray arr = new JsonArray();
        arr.add(argObj);
        base.add("arguments", arr);

        assertThrows(IllegalArgumentException.class, () -> new PlayCharacterCommand(base));
    }

    /**
     * Generates illegal argument objects to be fed to PlayCharacterCommand's constructor. In order, the cases checked
     * are:
     *
     * <ol>
     *     <li>JsonNull</li>
     *     <li>empty object</li>
     *     <li>without character</li>
     *     <li>with illegal character</li>
     *     <li>with null character</li>
     *     <li>without steps</li>
     *     <li>with null steps</li>
     *     <li>with non array steps</li>
     *     <li>with steps array containing null</li>
     *     <li>with steps array containing non object</li>
     * </ol>
     */
    static Stream<JsonElement> invalidObjectSource() {
        return Stream.of(
                JsonNull.INSTANCE,
                JsonParser.parseString("{}"),
                JsonParser.parseString("{`steps`:[]}".replace('`', '"')),
                JsonParser.parseString("{`character`:`sium`,steps`:[]}".replace('`', '"')),
                JsonParser.parseString("{`character`:null,steps`:[]}".replace('`', '"')),
                JsonParser.parseString("{`character`:`CENTAUR`}".replace('`', '"')),
                JsonParser.parseString("{`character`:`CENTAUR`,steps:null}".replace('`', '"')),
                JsonParser.parseString("{`character`:`CENTAUR`,steps:`sium`}".replace('`', '"')),
                JsonParser.parseString("{`character`:`CENTAUR`,steps:[null]}".replace('`', '"')),
                JsonParser.parseString("{`character`:`CENTAUR`,steps:[`sium`]}".replace('`', '"'))
        );
    }
}