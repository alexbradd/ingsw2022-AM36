package it.polimi.ingsw.server.controller.commands;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Utilities used for testing UserCommands
 */
public class CommandTestUtils {
    /**
     * Wraps {@link Assertions#assertThrows(Class, Executable)}, inspecting also the cause of the exception instead of
     * the exception itself.
     *
     * @param exception The {@link Class} of the exception expected, passed through to
     *                  {@link Assertions#assertThrows(Class, Executable)}
     * @param cause     The {@link Class} of the cause
     * @param exec      The {@link Executable} that will produce an exception, passed through to
     *                  {@link Assertions#assertThrows(Class, Executable)}
     * @param <T>       the type of the exception
     * @param <U>       the type of the cause
     * @throws AssertionError if assertion fails
     */
    static <T extends Throwable, U extends Throwable> void assertCause(Class<T> exception, Class<U> cause, Executable exec) {
        Throwable causeException = assertThrows(exception, exec).getCause();
        if (!cause.isInstance(causeException))
            throw new AssertionError(
                    "cause was not of valid type: excepted " +
                            cause.getSimpleName() +
                            ", got " +
                            causeException.getClass().getSimpleName());
    }
}
