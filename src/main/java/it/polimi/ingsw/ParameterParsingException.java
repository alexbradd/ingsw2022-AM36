package it.polimi.ingsw;

/**
 * Utility exception used in CLI parameter parsing.
 *
 * @see Main
 */
public class ParameterParsingException extends Exception {
    /**
     * {@inheritDoc}
     */
    public ParameterParsingException(String message) {
        super(message);
    }

    /**
     * Returns a correctly formatted exception with a printable message describing an unrecognized parameter.
     *
     * @param invalidOption the unrecognized parameter
     * @return a correctly formatted ParameterParsingException
     */
    static ParameterParsingException invalidOption(String invalidOption) {
        return new ParameterParsingException("Unrecognized option " + invalidOption);
    }

    /**
     * Returns a correctly formatted exception with a printable message describing an invalid parameter for some option.
     *
     * @param invalidParameter the invalid parameter
     * @param option           the option for which the parameter is
     * @return a correctly formatted ParameterParsingException
     */
    static ParameterParsingException invalidParameter(String invalidParameter, String option) {
        return new ParameterParsingException("Badly formatted parameter for " + option + ": " + invalidParameter);
    }

    /**
     * Returns a correctly formatted exception with a printable message describing a missing parameter for some option.
     *
     * @param option the option for which the parameter is missing
     * @return a correctly formatted ParameterParsingException
     */
    static ParameterParsingException missingParameter(String option) {
        return new ParameterParsingException("Missing parameter for " + option);
    }
}
