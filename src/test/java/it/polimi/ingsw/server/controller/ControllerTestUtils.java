package it.polimi.ingsw.server.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * A class that contains some util methods to generate JsonObject commands, used in other test classes, such as
 * {@link MatchRegistryTest} or {@link CommandManagerTest}.
 */
public class ControllerTestUtils {

    /**
     * Helper method for creating a CREATE command
     * @param name the name of the player
     * @param nPlayers the number of player of the Match
     * @param expert whether the Match should be in expert mode or not
     * @return the CREATE command
     */
    public static JsonObject generateCreate(String name, int nPlayers, boolean expert) {
        JsonObject command = new JsonObject();
        command.addProperty("type", "CREATE");
        command.addProperty("username", name);

        JsonObject argument = new JsonObject();
        argument.addProperty("nPlayers", nPlayers);
        argument.addProperty("expert", expert);

        JsonArray arguments = new JsonArray();
        arguments.add(argument);

        command.add("arguments", arguments);

        return command;
    }

    /**
     * Helper method for creating a JOIN command
     * @param name the name of the player
     * @param id the id of the Match to join
     * @return the JOIN command
     */
    public static JsonObject generateJoin(String name, int id) {
        JsonObject command = new JsonObject();
        command.addProperty("type", "JOIN");
        command.addProperty("username", name);
        command.addProperty("gameId", id);

        return command;
    }

    /**
     * Helper method for creating a LEAVE command
     * @param name the name of the player
     * @param id the id of the Match to leave
     * @return the LEAVE command
     */
    public static JsonObject generateLeave(String name, int id) {
        JsonObject command = new JsonObject();
        command.addProperty("type", "LEAVE");
        command.addProperty("username", name);
        command.addProperty("gameId", id);

        return command;
    }
}
