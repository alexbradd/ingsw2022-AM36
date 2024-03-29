package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.Mage;

import java.util.Objects;

/**
 * This class models the game's assistant cards.
 * It holds an {@link AssistantType} enumeration to store the card value
 * in order to deny an incorrect initialization of the assistant's values.
 *
 * @author Mattia Busso
 * @see AssistantType
 */
public class Assistant {

    /**
     * Assistant's type represented by its {@code AssistantType}
     */
    private final AssistantType assistantType;

    /**
     * {@link Mage} associated with the assistant.
     */
    private final Mage mage;

    /**
     * Basic constructor.
     *
     * @param assistantType the given {@link AssistantType}
     * @param mage          the given {@link Mage} of the card
     */
    Assistant(AssistantType assistantType, Mage mage) {
        this.assistantType = assistantType;
        this.mage = mage;
    }

    /**
     * {@code orderValue} getter.
     *
     * @return the card's value
     */
    public int getOrderValue() {
        return assistantType.getValue();
    }

    /**
     * {@code mNSteps} getter.
     *
     * @return the card's mNSteps
     */
    int getMNSteps() {
        return assistantType.getMNSteps();
    }

    /**
     * {@code mage} getter.
     *
     * @return the card's mage
     */
    Mage getMage() {
        return mage;
    }

    /**
     * {@code AssistantType} getter.
     *
     * @return the card's assistant type
     */
    AssistantType getAssistantType() {
        return assistantType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assistant assistant = (Assistant) o;
        return assistantType == assistant.assistantType && mage == assistant.mage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(assistantType, mage);
    }
}
