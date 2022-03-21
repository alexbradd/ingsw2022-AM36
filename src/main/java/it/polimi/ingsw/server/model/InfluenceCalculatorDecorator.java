package it.polimi.ingsw.server.model;

import java.util.Optional;

/**
 * Represents a generic decorator for {@link InfluenceCalculator}.
 *
 * @author Alexandru Gabriel Bradatan
 */
abstract class InfluenceCalculatorDecorator implements InfluenceCalculator {
    private final InfluenceCalculator calculator;

    /**
     * Creates a new decorator for the given {@link InfluenceCalculator}.
     *
     * @param toDecorate the {@link InfluenceCalculator} to decorate
     * @throws IllegalArgumentException if {@code toDecorate} is null
     */
    public InfluenceCalculatorDecorator(InfluenceCalculator toDecorate) {
        if (toDecorate == null) throw new IllegalArgumentException("toDecorate shouldn't be null");
        calculator = toDecorate;
    }

    /**
     * Returns and {@link Optional} containing the decorated calculator
     *
     * @return the decorated calculator
     */
    protected InfluenceCalculator getCalculator() {
        return calculator;
    }
}
