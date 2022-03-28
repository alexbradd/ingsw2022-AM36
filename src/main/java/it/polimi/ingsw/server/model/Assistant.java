package it.polimi.ingsw.server.model;

/**
 * This class models the game's assistant cards.
 *
 * @author Mattia Busso
 */
class Assistant {

    /**
     * Value associated with the assistant card.
     */
    private final int orderValue;

    /**
     * The card's correspondent maximum number of steps mother nature can perform.
     */
    private final int mNSteps;

    /**
     * Mage associated with the assistant.
     */
    private final Mage mage;

    /**
     * Assistant card constructor.
     *
     * @param orderValue
     * @param mNSteps
     * @param mage
     */
    Assistant(int orderValue, int mNSteps, Mage mage) {
        this.orderValue = orderValue;
        this.mNSteps = mNSteps;
        this.mage = mage;
    }

    /**
     * orderValue getter.
     *
     * @return orderValue
     */
    int getOrderValue() {
        return orderValue;
    }

    /**
     * mNSteps getter.
     *
     * @return mNSteps
     */
    int getMnSteps() {
        return mNSteps;
    }

    /**
     * Mage getter.
     *
     * @return mage
     */
    Mage getMage() {
        return mage;
    }

}
