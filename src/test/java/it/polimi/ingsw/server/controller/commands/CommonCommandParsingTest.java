package it.polimi.ingsw.server.controller.commands;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.model.Phase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.stream.Stream;

import static it.polimi.ingsw.server.controller.commands.CommandTestUtils.assertCause;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class that tests paring of all JsonObject attributes common between all AbstractCommand classes. Classes are
 * fetched dynamically using reflection from {@link it.polimi.ingsw.server.controller.commands} and tested for all
 * failure points regarding: "gameId", "type" and "username" properties.
 */
class CommonCommandParsingTest {
    /**
     * Check that null throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNull(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance((Object) null));
    }

    /**
     * Check that creation with a JsonObject without the "type" attribute throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withoutType(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj));
    }

    /**
     * Check that creation with a JsonObject where the "type" attribute is not a string throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withNonStringType(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", 1);
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj));
    }

    /**
     * Check that creation with a JsonObject where the "type" attribute is not a string throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withStringNullType(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", (String) null);
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj));
    }

    /**
     * Check that creation with a JsonObject where the "type" attribute is not the correct string throws
     * IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withWrongType(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "JOIN");
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class, UserCommandType.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj, UserCommandType.JOIN));
    }

    /**
     * Check that creation with a JsonObject where the "gameId" attribute is not present throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withoutId(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "JOIN");
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class, UserCommandType.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj, UserCommandType.JOIN));
    }

    /**
     * Check that creation with a JsonObject where the "gameId" attribute is not a Number throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withIdNotNumber(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "JOIN");
        obj.addProperty("gameId", (Number) null);
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class, UserCommandType.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj, UserCommandType.JOIN));
    }

    /**
     * Check that creation with a JsonObject where the "gameId" attribute is a Double throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withDoubleId(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "JOIN");
        obj.addProperty("gameId", 0.1);
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class, UserCommandType.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj, UserCommandType.JOIN));
    }

    /**
     * Check that creation with a JsonObject where the "gameId" attribute is negative throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withNegativeId(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "JOIN");
        obj.addProperty("gameId", -12);
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class, UserCommandType.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj, UserCommandType.JOIN));
    }

    /**
     * Check that creation with a JsonObject where the "username" attribute is not present throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withoutUsername(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "JOIN");
        obj.addProperty("gameId", 0);
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class, UserCommandType.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj, UserCommandType.JOIN));
    }

    /**
     * Check that creation with a JsonObject where the "username" attribute is not a valid String throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withNonStringUsername(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "JOIN");
        obj.addProperty("gameId", 0);
        obj.addProperty("username", 0);
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class, UserCommandType.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj, UserCommandType.JOIN));
    }

    /**
     * Check that creation with a JsonObject where the "username" attribute is a null String throws IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withNullStringUsername(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "JOIN");
        obj.addProperty("gameId", 0);
        obj.addProperty("username", (String) null);
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class, UserCommandType.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj, UserCommandType.JOIN));
    }

    /**
     * Check that creation with a JsonObject where the "username" attribute is an empty String throws
     * IllegalArgumentException
     */
    @ParameterizedTest
    @MethodSource("abstractCommandSource")
    void withNonNull_withEmptyStringUsername(Class<? extends AbstractCommand> cls) throws NoSuchMethodException {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "JOIN");
        obj.addProperty("gameId", 0);
        obj.addProperty("username", "");
        Constructor<? extends AbstractCommand> constructor = cls.getConstructor(JsonObject.class, UserCommandType.class);
        assertCause(InvocationTargetException.class, IllegalArgumentException.class, () -> constructor.newInstance(obj, UserCommandType.JOIN));
    }

    /**
     * Check that the base parsing of the classes tested is correct when passed a correctly formatted object
     */
    @Test
    void withCorrectObj() {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "JOIN");
        obj.addProperty("gameId", 0);
        obj.addProperty("username", "ann");

        MockUserCommand cmd = new MockUserCommand(obj, UserCommandType.JOIN);
        assertEquals("JOIN", cmd.getType().toString());
        assertEquals("ann", cmd.getUsername());
        assertEquals(0L, cmd.getGameId());
    }

    /**
     * Test source generator
     */
    static Stream<Class<? extends AbstractCommand>> abstractCommandSource() {
        String packageName = "it.polimi.ingsw.server.controller.commands";
        Class<AbstractCommand> superclass = AbstractCommand.class;
        InputStream stream = superclass.getClassLoader()
                .getResourceAsStream("../classes/" + packageName.replaceAll("[.]", "/"));
        assert stream != null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(line -> getClass(line, packageName))
                .filter(c -> Objects.equals(c.getSuperclass(), superclass))
                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                .map(c -> c.asSubclass(superclass));
    }

    /**
     * Utility method, wraps {@link Class#forName(String)} in a try-catch
     */
    static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("err", e);
        }
    }

    /**
     * Mock class used for simply creating a concrete instance of {@link AbstractCommand}
     */
    private static class MockUserCommand extends AbstractCommand {
        MockUserCommand(JsonObject cmd, UserCommandType type) {
            super(cmd, type);
        }

        @Override
        public Phase execute(Phase phase) {
            return null;
        }

        @Override
        public String getModificationMessage() {
            return null;
        }
    }
}