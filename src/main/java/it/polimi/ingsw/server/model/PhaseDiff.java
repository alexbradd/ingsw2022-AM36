package it.polimi.ingsw.server.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.List;

/**
 * Represents a difference between two Phases
 *
 * @author Alexndru Gabriel Bradatan
 * @see Phase
 */
public class PhaseDiff implements Jsonable {
    /**
     * The map of attribute name - attribute value pairs
     */
    private final HashMap<String, JsonPrimitive> attributes;
    /**
     * The map of key - list of Jsonables
     */
    private final HashMap<String, List<Jsonable>> entityUpdates;

    /**
     * Base constructor
     */
    public PhaseDiff() {
        this.attributes = new HashMap<>();
        this.entityUpdates = new HashMap<>();
    }

    /**
     * Getter for the map of attributes
     *
     * @return the map of attributes
     */
    public HashMap<String, JsonPrimitive> getAttributes() {
        return new HashMap<>(attributes);
    }

    /**
     * Getter for the map of entity updates
     *
     * @return the map of entity updates
     */
    public HashMap<String, List<Jsonable>> getEntityUpdates() {
        return new HashMap<>(entityUpdates);
    }

    /**
     * Adds a List of Jsonables identified by the given key to the diff
     *
     * @param key     the key by which the List will be identified
     * @param updates the list of Jsonables to add
     * @throws IllegalArgumentException if any parameter is null
     */
    public void addEntityUpdate(String key, List<Jsonable> updates) {
        if (key == null) throw new IllegalArgumentException("string shouldn't be null");
        if (updates == null) throw new IllegalArgumentException("string shouldn't be null");
        entityUpdates.put(key, updates);
    }

    /**
     * Adds an attribute identified by the given name to the diff
     *
     * @param name  the name of the attribute
     * @param value the JsonPrimitive containing the value of the parameter
     * @throws IllegalArgumentException if any parameter is null
     */
    public void addAttribute(String name, JsonPrimitive value) {
        if (name == null) throw new IllegalArgumentException("string shouldn't be null");
        if (value == null) throw new IllegalArgumentException("string shouldn't be null");
        attributes.put(name, value);
    }

    /**
     * Creates and return a new JsonObject corresponding to this object
     *
     * @return a JsonObject corresponding to this object
     */
    @Override
    public JsonElement toJson() {
        JsonObject ret = new JsonObject();
        entityUpdates.forEach((key, value) -> {
            JsonArray array = new JsonArray();
            for (Jsonable j : value)
                array.add(j.toJson());
            ret.add(key, array);
        });
        attributes.forEach(ret::add);
        return ret;
    }
}
