package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

import java.util.HashMap;

/**
 * Represents set of named parameters for a {@link Character} card effect step. Which parameters each card takes is
 * detailed in the card's documentation.
 *
 * @author Alexandru Gabriel Bradatan
 * @see Character
 */
public class CharacterStep {
    /**
     * Internal key-value map
     */
    private final HashMap<String, String> params;

    /**
     * Default constructor. Initializes internal data structures
     */
    public CharacterStep() {
        params = new HashMap<>();
    }

    /**
     * Adds a key-value pair to the object. If a key with that name already exists it wil be overwritten.
     *
     * @param name  parameter name
     * @param value parameter value
     * @throws IllegalArgumentException if any parameter is null
     */
    public void setParameter(String name, String value) {
        if (name == null) throw new IllegalArgumentException("name shouldn't be null");
        if (value == null) throw new IllegalArgumentException("value shouldn't be null");
        params.put(name, value);
    }

    /**
     * Returns the values associated with the given name. If there is none, null is returned
     *
     * @param name the name to search
     * @return the String associated to {@code name} or null
     */
    public String getParameter(String name) {
        return params.get(name);
    }

    /**
     * Interpret the value of the parameter with the given name as a {@link Island} index verify that it is valid in
     * the context of the given {@link ActionPhase}.
     *
     * @param name  parameter name
     * @param phase ActionPhase from where to retrieve the Island
     * @return the Island index
     * @throws InvalidCharacterParameterException if {@code name} is not in the map
     * @throws InvalidCharacterParameterException if the value corresponding to {@code name} is not a valid integer
     * @throws InvalidCharacterParameterException if the index corresponding to {@code name} is not a valid Island index
     * @throws IllegalArgumentException           if any parameter is null
     */
    int getParameterAsIslandIndex(String name, ActionPhase phase) throws InvalidCharacterParameterException {
        if (name == null) throw new IllegalArgumentException("name shouldn't be null");
        if (phase == null) throw new IllegalArgumentException("phase shouldn't be null");
        String value = getValue(name);
        try {
            int islandIndex = Integer.parseInt(value);
            if (phase.isValidIslandIndex(islandIndex))
                return islandIndex;
            throw new InvalidCharacterParameterException(formatExceptionMessage(name, value));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            throw new InvalidCharacterParameterException(formatExceptionMessage(name, value), e);
        }
    }

    /**
     * Casts the parameter with the given name to a {@link PieceColor}.
     *
     * @param name parameter name
     * @return {@link PieceColor} corresponding
     * @throws InvalidCharacterParameterException if {@code name} isn't in the map or the value corresponding to it is
     *                                            not a valid {@link PieceColor}
     * @throws IllegalArgumentException           if {@code name} is null
     */
    PieceColor getParameterAsColor(String name) throws InvalidCharacterParameterException {
        if (name == null) throw new IllegalArgumentException("name shouldn't be null");
        String value = getValue(name);
        try {
            return PieceColor.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidCharacterParameterException(formatExceptionMessage(name, value), e);
        }
    }

    /**
     * Private helper to retrieve a parameter value. If the key is not found, {@link InvalidCharacterParameterException}
     * is thrown.
     *
     * @param name parameter name
     * @return the parameter value
     * @throws InvalidCharacterParameterException if no parameter with the given name is stored in the object
     */
    private String getValue(String name) throws InvalidCharacterParameterException {
        if (!params.containsKey(name))
            throw new InvalidCharacterParameterException(name + " is not contained in this map");
        return params.get(name);
    }

    /**
     * Helper that formats the exception message
     *
     * @param name  name of the parameter
     * @param value value of the parameter
     * @return formatted Exception message
     */
    private String formatExceptionMessage(String name, String value) {
        return "invalid parameter " + value + " for key " + name;
    }
}