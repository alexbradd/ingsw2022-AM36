package it.polimi.ingsw.server.model;

public class Herbalist {
    private int blocks;

    Herbalist() {
        blocks = 4;
    }

    int getNumOfBlocks() {
        return blocks;
    }

    void pushBlock(BlockCard c) {
        blocks++;
    }

    BlockCard popBlock() {
        blocks--;
        return new BlockCard(this);
    }
}
