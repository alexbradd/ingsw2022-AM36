package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.CharacterType;

class MockCharacter extends Character {

    /**
     * Base constructor. Sets up only the card's initial cost and character
     *
     * @throws IllegalArgumentException if {@code characterType} is null
     */
    MockCharacter() {
        super(CharacterType.HERBALIST);
    }

    MockCharacter(MockCharacter old) {
        super(old);
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    Character shallowCopy() {
        return new MockCharacter(this);
    }

}
