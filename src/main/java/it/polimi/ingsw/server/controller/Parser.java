package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;

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
     * Creates a non-game-specific error message with the given reason.
     *
     * @param reason a human-readable string describing why the error happened.
     * @return a {@link JsonObject} containing the error message
     */
    public static JsonObject buildErrorMessage(String reason) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", "ERROR");
        obj.addProperty("reason", reason);
        return obj;
    }

    /**
     * Creates an error message relative to a game with the given reason.
     *
     * @param gameId the id of the game this error message is relative to
     * @param reason a human-readable string describing why the error happened.
     * @return a {@link JsonObject} containing the error message
     */
    public static JsonObject buildErrorMessage(long gameId, String reason) {
        JsonObject ret = buildErrorMessage(reason);
        ret.addProperty("gameId", gameId);
        return ret;
    }
}
