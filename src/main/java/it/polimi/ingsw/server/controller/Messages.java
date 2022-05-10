package it.polimi.ingsw.server.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Static class containing utilities for working with JsonObject messages
 */
public class Messages {
    /**
     * Private constructor to keep the class static
     */
    private Messages() {
    }

    /**
     * Utility static method that extracts a JsonElement with the given key from the specified JsonObject.
     *
     * @param obj the JsonObject from which to extract the JsonElement
     * @param key the name of property from which to extract the JsonElement
     * @return the extracted JsonElement
     * @throws IllegalArgumentException if any parameter is null or {@code obj} is badly formatted
     */
    public static JsonElement extractElement(JsonObject obj, String key) {
        if (obj == null) throw new IllegalArgumentException("obj shouldn't be null");
        if (key == null) throw new IllegalArgumentException("key shouldn't be null");
        if (!obj.has(key)) throw new IllegalArgumentException("obj must have a '" + key + "' property");
        return obj.get(key);
    }

    /**
     * Utility static method that extracts a JsonArray with the given key from the specified JsonObject. The array is
     * also checked for a minimum size.
     *
     * @param obj     the JsonObject from which to extract the JsonElement
     * @param key     the name of property from which to extract the JsonElement
     * @param minSize the minimum size of the array
     * @return the extracted JsonArray
     * @throws IllegalArgumentException if any parameter is null, {@code minSize} is negative or {@code obj} is badly
     *                                  formatted
     */
    public static JsonArray extractArray(JsonObject obj, String key, int minSize) {
        if (minSize < 0)
            throw new IllegalArgumentException("minimum size should be positive");
        JsonElement arr = extractElement(obj, key);
        if (!arr.isJsonArray())
            throw new IllegalArgumentException("obj." + key + " should be an array");
        if (arr.getAsJsonArray().size() < minSize)
            throw new IllegalArgumentException("obj." + key + " should have at least " + minSize + " elements");
        return arr.getAsJsonArray();
    }

    /**
     * Utility static method that extracts a string with the given key from the specified JsonObject.
     *
     * @param obj the JsonObject from which to extract the string
     * @param key the name of property from which to extract the string
     * @return the extracted string
     * @throws IllegalArgumentException if any parameter is null or {@code obj} is badly formatted
     */
    public static String extractString(JsonObject obj, String key) {
        JsonElement keyElement = extractElement(obj, key);
        return asString(keyElement);
    }

    /**
     * Utility static method that converts a JsonElement into a String.
     *
     * @param elem the JsonElement to convert
     * @return the String contained inside the JsonElement
     * @throws IllegalArgumentException if {@code elem} is null or not a String
     */
    public static String asString(JsonElement elem) {
        if (elem == null) throw new IllegalArgumentException("elem shouldn't be null");
        if (!elem.isJsonPrimitive() || !elem.getAsJsonPrimitive().isString())
            throw new IllegalArgumentException(elem + " is not a valid string");
        return elem.getAsString();
    }

    /**
     * Utility static method that extracts a long with the given key from the specified JsonObject,
     *
     * @param obj the JsonObject from which to extract the long
     * @param key the name of property from which to extract the long
     * @return the extracted long
     * @throws IllegalArgumentException if any parameter is null or {@code obj} is badly formatted
     */
    public static long extractNumber(JsonObject obj, String key) {
        JsonElement keyElement = extractElement(obj, key);
        return asNumber(keyElement);
    }


    /**
     * Utility static method that converts a JsonElement into a Number, specifically a long.
     *
     * @param elem the JsonElement to convert
     * @return the Number contained inside the JsonElement
     * @throws IllegalArgumentException if {@code elem} is null or not a Number
     */
    public static long asNumber(JsonElement elem) {
        if (elem == null) throw new IllegalArgumentException("elem shouldn't be null");
        if (!elem.isJsonPrimitive() || !elem.getAsJsonPrimitive().isNumber())
            throw new IllegalArgumentException(elem + " is not a valid number");
        return elem.getAsLong();
    }
}
