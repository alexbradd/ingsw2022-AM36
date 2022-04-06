package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.CharacterType;

import java.util.function.Consumer;

class Herbalist extends Character {
    private int blocks;

    Herbalist() {
        super(CharacterType.HERBALIST);
        blocks = 4;
    }

    Herbalist(Herbalist h) {
        super(CharacterType.HERBALIST);
        this.blocks = h.blocks;
    }

    int getNumOfBlocks() {
        return blocks;
    }

    Character pushBlock(BlockCard c) {
        blocks++;
        return this;
    }

    Tuple<Character, BlockCard> popBlock() {
        blocks--;
        return new Tuple<>(this, new BlockCard(CharacterType.HERBALIST));
    }
}
