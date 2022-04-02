package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Represents a Herbalist card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Herbalist extends Character {
    /**
     * Flag for marking if the stack of blocks has been initialized
     */
    private boolean populatedBlocks;

    /**
     * Stack of blocks placed on the card
     */
    private final Stack<BlockCard> blocks;

    /**
     * Creates a new Herbalist card.
     */
    Herbalist() {
        this(false);
    }

    /**
     * Creates a new Herbalist card with pre-populated blocks. This constructor should be used in testing where the
     * {@link PreparePhase} hook is not available.
     *
     * @param prePopulateBlocks boolean indicating if blocks should be pre-populated.
     */
    Herbalist(boolean prePopulateBlocks) {
        super(Characters.HERBALIST, 2);
        blocks = new Stack<>();
        populatedBlocks = false;
        if (prePopulateBlocks) populateBlocks();
    }

    /**
     * {@link PreparePhase} hook. Populates internal store of blocks.
     *
     * @param phase the {@link PreparePhase} the card's hook has been called from
     * @throws IllegalArgumentException if {@code phase} is null
     */
    @Override
    void doPrepare(PreparePhase phase) {
        super.doPrepare(phase);
        populateBlocks();
    }

    /**
     * Populate stack of blocks with 4 blocks, if it hasn't been already done.
     */
    private void populateBlocks() {
        if (populatedBlocks) return;
        for (int i = 0; i < 4; i++)
            blocks.push(new BlockCard(this));
        populatedBlocks = true;
    }

    /**
     * Place a block card on the specified island. Parameter layout:
     *
     * <ul>
     *     <li>Position 0: island index to block</li>
     *     <li>Position 1...: ignored</li>
     * </ul>
     *
     * @param phase      the {@link ActionPhase} the card's effect has been called from
     * @param parameters an array of strings configuring the effect
     * @throws IllegalArgumentException           if any of the parameters are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code parameters} is formatted
     *                                            incorrectly
     */
    @Override
    void doEffect(ActionPhase phase, String[] parameters) throws InvalidCharacterParameterException {
        super.doEffect(phase, parameters);
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Adds the given {@link BlockCard} to the internal store.
     *
     * @param block the {@link BlockCard} to add to the store
     * @throws IllegalArgumentException if {@code block} is null or this card is not the owner of the block
     */
    @Override
    void pushBlock(BlockCard block) {
        if (block == null) throw new IllegalArgumentException("block shouldn't be null");
        if (!block.getOwner().equals(this)) throw new IllegalArgumentException("TODO");
        blocks.push(block);
    }

    /**
     * Pops a {@link BlockCard} from the internal store and returns it.
     *
     * @return a {@link BlockCard}
     * @throws IllegalStateException if there aren't any more blocks left on the card
     */
    @Override
    BlockCard popBlock() {
        try {
            return blocks.pop();
        } catch (EmptyStackException e) {
            throw new IllegalStateException("Cannot return any more blocks", e);
        }
    }

    /**
     * Returns true if this card contains this particular {@link BlockCard} in its store.
     *
     * @return true if this card contains this particular {@link BlockCard} in its store
     * @throws IllegalArgumentException if {@code block} is null
     */
    @Override
    boolean containsBlock(BlockCard block) {
        if (block == null) throw new IllegalArgumentException("block shouldn't be null");
        return blocks.contains(block);
    }

    /**
     * Returns the number of {@link BlockCard} available on this card.
     *
     * @return the number of {@link BlockCard} available on this card
     */
    @Override
    int getNumOfBlocks() {
        return blocks.size();
    }
}
