//STUB
package it.polimi.ingsw.server.model.enums;

public enum CharacterType {
    HERBALIST(2);

    private final int initialCost;

    CharacterType(int initialCost) {
        this.initialCost = initialCost;
    }

    public int getInitialCost() {
        return initialCost;
    }

}