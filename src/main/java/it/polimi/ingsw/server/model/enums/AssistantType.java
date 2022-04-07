package it.polimi.ingsw.server.model.enums;

import it.polimi.ingsw.server.model.Assistant;

/**
 * Assistant types enumeration.
 * Each {@code assistant} has an order {@code value} and a {@code mNSteps} value.
 * For the concrete implementation of the {@code Assistant} card (that include the card's {@link Mage}) see
 * {@link Assistant}.
 *
 * @author Mattia Busso
 * @see Assistant
 */
public enum AssistantType {
    CHEETAH(1, 1), OSTRICH(2, 1),
    CAT(3, 2), EAGLE(4, 2),
    FOX(5, 3), SNAKE(6, 3),
    OCTOPUS(7, 4), DOG(8, 4),
    ELEPHANT(9, 5), TURTLE(10, 5);

    /**
     * The value of the assistant type.
     */
    private final int value;

    /**
     * The {@code MN} number of steps of the assistant type
     */
    private final int mNSteps;

    AssistantType(int value, int mNSteps) {
        this.value = value;
        this.mNSteps = mNSteps;
    }

    /**
     * {@code value} getter.
     *
     * @return the {@code AssistantType}'s value
     */
    public int getValue() {
        return value;
    }

    /**
     * {@code mNSteps} getter
     *
     * @return the {@code AssistantType}'s {@code MN} steps
     */
    public int getMNSteps() {
        return mNSteps;
    }

}
