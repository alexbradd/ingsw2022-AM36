package it.polimi.ingsw.server.model.exceptions;

import it.polimi.ingsw.server.model.Phase;

/**
 * Represents an error caused by an incorrect call to a {@link Phase} modifier method.
 */
public class InvalidPhaseUpdateException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public InvalidPhaseUpdateException() {
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public InvalidPhaseUpdateException(String message) {
        super(message);
    }
}
