package it.polimi.ingsw.client.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GUIMessageBuilder {
    private final JsonObject message;

    public GUIMessageBuilder(String type) {
        message = new JsonObject();
        message.addProperty("type", type);
    }

    public GUIMessageBuilder addGameId(long id) {
        message.addProperty("gameId", id);
        return this;
    }

    public GUIMessageBuilder addUsername(String username) {
        message.addProperty("username", username);
        return this;
    }

    public GUIMessageBuilder addElement(String key, JsonElement obj) {
        message.add(key, obj);
        return this;
    }

    public JsonObject build() {
        return message;
    }
}
