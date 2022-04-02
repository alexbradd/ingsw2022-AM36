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

    /**
     * Static utility for constructing a meaningful message. The message is formatted as such:
     *
     * <pre>
     *     Invalid parameter passed to Character at {pos}: {detail}
     * </pre>
     *
     * @param pos    the position of the invalid parameter
     * @param detail a detailed explanation of why the parameter is invalid
     * @return a correctly formatted string to be used as the exception message
     */
    public static String message(int pos, String detail) {
        return "Invalid parameter passed to Character at " + pos + ": " + detail;
    }
}
