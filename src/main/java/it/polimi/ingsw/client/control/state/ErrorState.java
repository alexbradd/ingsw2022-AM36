package it.polimi.ingsw.client.control.state;

/**
 * Client's representation of the error state of the game.
 *
 * @author Mattia Busso
 */
public class ErrorState {

    /**
     * The reason of the error.
     */
    private String reason;

    /**
     * Returns the error's reason
     *
     * @return the error's reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "\n* ERROR *\n" + reason;
    }

}
