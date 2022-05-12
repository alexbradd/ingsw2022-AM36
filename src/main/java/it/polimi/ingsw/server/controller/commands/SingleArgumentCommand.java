package it.polimi.ingsw.server.controller.commands;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.function.Function;

import static it.polimi.ingsw.server.controller.Messages.extractArray;

/**
 * Abstract command that takes 1 argument from a list of arguments.
 *
 * @param <T> the final type of the argument
 */
abstract class SingleArgumentCommand<T> extends AbstractCommand {
    private final T arg;

    /**
     * Creates a new SingleArgumentCommand from the specified JsonObject. The JsonObject must have the following
     * properties:
     *
     * <ul>
     *     <li>{@code type} must be equal to the specified one</li>
     *     <li>{@code username} must be a non-null string</li>
     *     <li>{@code gameId} must be a {@link Number}</li>
     *     <li>{@code arguments} must be an array of strings with at least one value</li>
     * </ul>
     *
     * @param cmd       the JsonObject from which to create the command
     * @param type      the value that the {@code type} property of {@code cmd} must have
     * @param extractor a {@link Function} that converts a JsonElement into the desired type. In case it cannot convert
     *                  the value, it must throw {@link IllegalArgumentException}.
     * @throws IllegalArgumentException if any argument is null or {@code cmd} is not formatted correctly
     */
    SingleArgumentCommand(JsonObject cmd, UserCommandType type, Function<JsonElement, T> extractor) {
        super(cmd, type);
        JsonElement arg = extractArray(cmd, "arguments", 1).get(0);
        this.arg = extractor.apply(arg);
    }

    /**
     * Getter for the argument retrieved.
     *
     * @return the argument retrieved.
     */
    public T getArg() {
        return arg;
    }
}
