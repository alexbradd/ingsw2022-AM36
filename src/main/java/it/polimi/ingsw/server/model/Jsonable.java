package it.polimi.ingsw.server.model;

import com.google.gson.JsonElement;

/**
 * Interface that defines an object that can be converted to JSON
 */
public interface Jsonable {
    /**
     * Creates and return a new JsonElement corresponding to this object
     *
     * @return a JsonElement corresponding to this object
     */
    JsonElement toJson();
}
