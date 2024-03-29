package it.polimi.ingsw.server.model.exceptions;

import it.polimi.ingsw.server.model.Player;

/**
 * Represents an error caused by an attempt by a {@link Player} of executing a command when he doesn't have permission
 * to do so.
 */
public class InvalidPlayerException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public InvalidPlayerException() {
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public InvalidPlayerException(String message) {
        super(message);
    }
}
