package it.polimi.ingsw.server.controller.commands;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.controller.Messages;

/**
 * Static class that parses {@link JsonObject} into {\@link UserCommand} objects.
 */
public class Parser {
    /**
     * Class shouldn't be instantiated.
     */
    private Parser() {
    }

    /**
     * Parses the given JsonObject into a {@link UserCommand}
     *
     * @param object the object to parse
     * @return a new {@link UserCommand}
     * @throws IllegalArgumentException if {@code object} is null or formatted incorrectly
     */
    public static UserCommand parse(JsonObject object) {
        if (object == null) throw new IllegalArgumentException("object shouldn't be null");
        if (!object.has("type"))
            throw new IllegalArgumentException("object needs to have a 'type' property");
        String typeStr = Messages.asString(object.get("type"));
        UserCommandType type = UserCommandType.valueOf(typeStr);
        return switch (type) {
            case JOIN -> new JoinCommand(object);
            case LEAVE -> new LeaveCommand(object);
            case CHOOSE_MAGE -> new ChooseMageCommand(object);
            case PLAY_ASSISTANTS -> new PlayAssistantsCommand(object);
            case MOVE_STUDENT -> new MoveStudentCommand(object);
            case PLAY_CHARACTER -> new PlayCharacterCommand(object);
            case MOVE_MN -> new MoveMnCommand(object);
            case PICK_CLOUD -> new PickCloudCommand(object);
        };
    }
}
