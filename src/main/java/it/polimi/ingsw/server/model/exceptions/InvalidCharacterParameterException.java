
package it.polimi.ingsw.server.model.exceptions;

/**
 * Represents an error during parsing of the arguments passed to {@link Character}'s {@code doEffect()}.
 *
 * @author Alexandru Gabriel Bradatan
 */
public class InvalidCharacterParameterException extends Exception {
    /**
     * @inheritDocs
     */
    public InvalidCharacterParameterException(String message) {
        super(message);
    }

    /**
     * @inheritDocs
     */
    public InvalidCharacterParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}