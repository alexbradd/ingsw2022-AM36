package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.CharacterType;

abstract class Character {
    private final CharacterType type;

    Character(CharacterType type) {
        this.type = type;
    }

    CharacterType getCharacterType() {
        return type;
    }

    int getCost() {
        return type.getInitialCost();
    }

    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep[] params) {
        return new Tuple<>(phase, this);
    }

    Character pushBlock(BlockCard c) {
        throw new UnsupportedOperationException();
    }

    Tuple<Character, BlockCard> popBlock() {
        throw new UnsupportedOperationException();
    }
}
