package it.polimi.ingsw.server.model;

/**
 * Assistant stub
 */
class Assistant {
    int orderValue;

    Assistant(int orderValue, int mNSteps, Mage mage) {
        this.orderValue = orderValue;
    }

    int getOrderValue() {
        return orderValue;
    }

}
