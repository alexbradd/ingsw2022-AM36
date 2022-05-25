package it.polimi.ingsw.server.controller.commands;

import com.google.gson.*;
import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.Mage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests basic additional parameter parsing for the commands that take 1 argument.
 *
 * @see CommonCommandParsingTest
 */
class SingleArgumentCommandTest {
    private JsonObject base;

    /**
     * Creates a new JsonObject before each test
     */
    @BeforeEach
    void setup() {
        base = new JsonObject();
        base.addProperty("gameId", 0);
        base.addProperty("type", "MOCK");
        base.addProperty("username", "ann");
    }

    /**
     * Check that creation with a JsonObject where the "arguments" attribute is not present throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("wrongTestSource")
    void withNonNull_noArguments(WrongTestCase wrongTestCase) {
        assertThrows(IllegalArgumentException.class, () -> wrongTestCase.exec.accept(base));
    }

    /**
     * Check that creation with a JsonObject where the "arguments" attribute is not an array throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("wrongTestSource")
    void withNonNull_argumentsNotArray(WrongTestCase wrongTestCase) {
        base.addProperty("arguments", 1);
        assertThrows(IllegalArgumentException.class, () -> wrongTestCase.exec.accept(base));
    }

    /**
     * Check that creation with a JsonObject where the "arguments" array is not long enough throws
     * IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("wrongTestSource")
    void withNonNull_argumentsTooShort(WrongTestCase wrongTestCase) {
        JsonArray arr = new JsonArray();
        base.add("arguments", arr);
        assertThrows(IllegalArgumentException.class, () -> wrongTestCase.exec.accept(base));
    }

    /**
     * Check that creation with a JsonObject where the first element of "arguments" array is not a string throws
     * IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("wrongTestSource")
    void withNonNull_argumentsFirstWrongType(WrongTestCase wrongTestCase) {
        JsonArray arr = new JsonArray();
        arr.add(wrongTestCase.wrongTypeSrc);
        base.add("arguments", arr);
        assertThrows(IllegalArgumentException.class, () -> wrongTestCase.exec.accept(base));
    }

    /**
     * Check that creation with a JsonObject where the first element of "arguments" array is not a valid Mage throws
     * IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("wrongTestSource")
    void withNonNull_argumentsFirstWrongValue(WrongTestCase wrongTestCase) {
        JsonArray arr = new JsonArray();
        arr.add(wrongTestCase.wrongSrc);
        base.add("arguments", arr);
        assertThrows(IllegalArgumentException.class, () -> wrongTestCase.exec.accept(base));
    }

    /**
     * Generates a Stream of WrongTestCase used for test parametrization
     */
    static Stream<WrongTestCase> wrongTestSource() {
        return Stream.of(
                new WrongTestCase(JsonNull.INSTANCE, new JsonPrimitive("not a mage"), (obj) -> new ChooseMageCommand(obj, UserCommandType.JOIN)),
                new WrongTestCase(JsonNull.INSTANCE, new JsonPrimitive("not a assistant"), (obj) -> new PlayAssistantsCommand(obj, UserCommandType.JOIN)),
                new WrongTestCase(JsonNull.INSTANCE, new JsonPrimitive(-1), (obj) -> new MoveMnCommand(obj, UserCommandType.JOIN)),
                new WrongTestCase(JsonNull.INSTANCE, new JsonPrimitive(-1), (obj) -> new PickCloudCommand(obj, UserCommandType.JOIN)),
                new WrongTestCase(JsonNull.INSTANCE, new JsonObject(), (obj) -> new MoveStudentCommand(obj, UserCommandType.JOIN)),
                new WrongTestCase(JsonNull.INSTANCE, new JsonObject(), (obj) -> new PlayCharacterCommand(obj, UserCommandType.JOIN))
        );
    }

    /**
     * Checks that a SingleArgumentUserCommand is correctly created when passed a correct object
     * <p>
     * Note: this test checks for correctness only for the more basic objects. For more detailed testing, see each
     * class's specific test
     */
    @ParameterizedTest
    @MethodSource("correctTestSource")
    void withCorrectObject(CorrectTestCase<?> arg) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<? extends SingleArgumentCommand<?>> constructor = arg.clazz.getConstructor(JsonObject.class, UserCommandType.class);
        SingleArgumentCommand<?> cmd = constructor.newInstance(arg.obj, UserCommandType.JOIN);

        assertEquals(UserCommandType.JOIN, cmd.getType());
        assertEquals(0L, cmd.getGameId());
        assertEquals("ann", cmd.getUsername());
        assertEquals(arg.correctArg, cmd.getArg());
    }

    /**
     * Checks that {@link SingleArgumentCommand#getModificationMessage()} does not return null or an empty string.
     */
    @ParameterizedTest
    @MethodSource("correctTestSource")
    void withCorrectObject_modificationMessageValidString(CorrectTestCase<?> arg) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor<? extends SingleArgumentCommand<?>> constructor = arg.clazz.getConstructor(JsonObject.class, UserCommandType.class);
        SingleArgumentCommand<?> cmd = constructor.newInstance(arg.obj, UserCommandType.JOIN);

        String msg = cmd.getModificationMessage();
        assertNotNull(msg);
        assertNotEquals("", msg);
    }

    /**
     * Supplies {@link #withCorrectObject(CorrectTestCase)} with correctly formatted JsonObjects
     */
    static Stream<CorrectTestCase<?>> correctTestSource() {
        return Stream.of(
                new CorrectTestCase<>(JsonParser.parseString("[\"MAGE\"]"), ChooseMageCommand.class, Mage.MAGE),
                new CorrectTestCase<>(JsonParser.parseString("[\"CAT\"]"), PlayAssistantsCommand.class, AssistantType.CAT),
                new CorrectTestCase<>(JsonParser.parseString("[0]"), MoveMnCommand.class, 0),
                new CorrectTestCase<>(JsonParser.parseString("[0]"), PickCloudCommand.class, 0)
        );
    }

    /**
     * Simple data holder for data used in wrong-object testing
     */
    private static class WrongTestCase {
        /**
         * JsonElement containing an element of the wrong type.
         */
        public JsonElement wrongTypeSrc;
        /**
         * JsonElement containing an element of the correct type but invalid value.
         */
        public JsonElement wrongSrc;
        /**
         * Consumes the JsonObject constructed in the test method
         */
        public ThrowingConsumer<JsonObject> exec;

        /**
         * Creates a new WrongTestCase
         */
        public WrongTestCase(JsonElement wrongTypeSrc, JsonElement wrongSrc, ThrowingConsumer<JsonObject> exec) {
            this.wrongTypeSrc = wrongTypeSrc;
            this.wrongSrc = wrongSrc;
            this.exec = exec;
        }
    }

    /**
     * Simple data holder for data used in correct-object testing
     *
     * @param <T> the type of the argument of the SingleArgumentCommand tested
     */
    private static class CorrectTestCase<T> {
        public JsonObject obj;
        public Class<? extends SingleArgumentCommand<T>> clazz;
        public T correctArg;

        public CorrectTestCase(JsonElement args, Class<? extends SingleArgumentCommand<T>> clazz, T correctArg) {
            JsonObject base = new JsonObject();
            base.addProperty("gameId", 0);
            base.addProperty("type", "JOIN");
            base.addProperty("username", "ann");
            base.add("arguments", args);
            this.obj = base;
            this.clazz = clazz;
            this.correctArg = correctArg;
        }
    }
}