package it.polimi.ingsw.server.controller.commands;

import com.google.gson.JsonObject;

import static it.polimi.ingsw.server.controller.Messages.extractNumber;
import static it.polimi.ingsw.server.controller.Messages.extractString;

/**
 * Represents a generic command.
 */
abstract class AbstractCommand implements UserCommand {
    /**
     * The {@link UserCommandType} associated with this instance.
     */
    private final UserCommandType type;
    /**
     * The id of the game this command is tied to
     */
    private final long gameId;
    /**
     * The username of the player this command is tied to.
     */
    private final String username;

    /**
     * Creates a new AbstractCommand from the specified JsonObject. The JsonObject must have the following properties:
     *
     * <ul>
     *     <li>{@code type} must be equal to the specified one</li>
     *     <li>{@code username} must be a non-null string</li>
     *     <li>{@code gameId} must be a {@link Number}</li>
     * </ul>
     *
     * @param cmd  the JsonObject from which to create the command
     * @param type the value that the {@code type} property of {@code cmd} must have
     * @throws IllegalArgumentException if any argument is null or {@code cmd} is not formatted correctly
     */
    AbstractCommand(JsonObject cmd, UserCommandType type) {
        String typeStr = extractString(cmd, "type");
        UserCommandType retrievedType = UserCommandType.valueOf(typeStr);
        if (type != retrievedType)
            throw new IllegalArgumentException("object does not have the correct type");
        this.type = retrievedType;
        long retrievedId = extractNumber(cmd, "gameId");
        if (retrievedId < 0)
            throw new IllegalArgumentException("gameId should be a positive number");
        this.gameId = retrievedId;
        String retrievedUsername = extractString(cmd, "username");
        if (retrievedUsername.equals(""))
            throw new IllegalArgumentException("username should be a non-null string");
        this.username = retrievedUsername;
    }

    /**
     * Returns the username associated with this command
     *
     * @return the username associated with this command
     */
    @Override
    public String getUsername() {
        return username;
    }

    /**
     * Returns the id of the game this command is for.
     *
     * @return the id of the game this command is for.
     */
    @Override
    public long getGameId() {
        return gameId;
    }

    /**
     * Returns the {@link UserCommandType} associated with this instance
     *
     * @return the {@link UserCommandType} associated with this instance
     */
    @Override
    public UserCommandType getType() {
        return type;
    }
}
