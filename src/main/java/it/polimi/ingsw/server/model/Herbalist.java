package it.polimi.ingsw.server.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a Herbalist card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Herbalist extends Character {

    /**
     * Stack of blocks placed on the card
     */
    private ArrayList<BlockCard> blocks;

    /**
     * Creates a new Herbalist card.
     */
    Herbalist() {
        super(CharacterType.HERBALIST);
        blocks = new ArrayList<>();
    }

    /**
     * Returns a shallow copy of the given Herbalist
     *
     * @param old the Herbalist to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private Herbalist(Herbalist old) {
        super(old);
        blocks = old.blocks;
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    Herbalist shallowCopy() {
        return new Herbalist(this);
    }

    /**
     * {@link PreparePhase} hook. Populates internal store of blocks.
     *
     * @param phase the {@link PreparePhase} the card's hook has been called from
     * @return a Tuple containing the updated PreparePhase and the updated Character
     * @throws IllegalArgumentException if {@code phase} is null
     */
    @Override
    Tuple<PreparePhase, Character> doPrepare(PreparePhase phase) {
        super.doPrepare(phase);
        Herbalist h = this.shallowCopy();
        h = h.populateBlocks();
        return new Tuple<>(phase, h);
    }

    /**
     * Populate stack of blocks with 4 blocks, if it hasn't been already done.
     */
    private Herbalist populateBlocks() {
        Herbalist h = this.shallowCopy();
        h.blocks = new ArrayList<>(h.blocks);
        for (int i = 0; i < 4; i++)
            h.blocks.add(new BlockCard(CharacterType.HERBALIST));
        return h;
    }

    /**
     * Place a block card on the specified island. This card uses 1 step, any more will be ignored. The parameters used
     * in the step are the following:
     *
     * <ul>
     *     <li>island: island index to block</li>
     * </ul>
     *
     * @param phase the {@link ActionPhase} the card's effect has been called from
     * @param steps an array of {@link CharacterStep} configuring the effect
     * @return a Tuple containing the updated ActionPhase and the updated Character
     * @throws IllegalArgumentException           if {@code phase} or {@code steps} or are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code steps} is formatted incorrectly
     */
    @Override
    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep[] steps) throws InvalidCharacterParameterException {
        checkEffectParameters(phase, steps, 1);
        int islandIndex = steps[0].getParameterAsIslandIndex("island", phase);
        if (getNumOfBlocks() == 0)
            throw new InvalidCharacterParameterException("Wrong invocation: no more blocks on this card");
        return super.doEffect(phase, steps)
                .map(t -> {
                    ActionPhase p = t.getFirst();
                    Herbalist h = (Herbalist) t.getSecond();
                    return h.popBlock()
                            .map(v -> {
                                BlockCard b = v.getSecond();
                                ActionPhase afterBlock = p.blockIsland(islandIndex, b);
                                return new Tuple<>(afterBlock, v.getFirst());
                            });
                });
    }

    /**
     * Adds the given {@link BlockCard} to the internal store.
     *
     * @param block the {@link BlockCard} to add to the store
     * @return the updated Herbalist
     * @throws IllegalArgumentException if {@code block} is null or this card is not the owner of the block
     */
    @Override
    Herbalist pushBlock(BlockCard block) {
        if (block == null) throw new IllegalArgumentException("block shouldn't be null");
        if (!block.getOwner().equals(this.getCharacterType()))
            throw new IllegalArgumentException("Why are you stealing blocks bruh");
        Herbalist h = this.shallowCopy();
        h.blocks = new ArrayList<>(h.blocks);
        h.blocks.add(block);
        return h;
    }

    /**
     * Pops a {@link BlockCard} from the internal store and returns it.
     *
     * @return a Tuple containing the new Character and a BlockCard.
     * @throws IllegalStateException if there aren't any more blocks left on the card
     */
    @Override
    Tuple<Character, BlockCard> popBlock() {
        if (getNumOfBlocks() == 0)
            throw new IllegalStateException("Cannot return any more blocks");
        Herbalist h = this.shallowCopy();
        h.blocks = new ArrayList<>(h.blocks);
        BlockCard b = h.blocks.remove(0);
        return new Tuple<>(h, b);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Herbalist herbalist = (Herbalist) o;
        return blocks.size() == herbalist.blocks.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), blocks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement toJson() {
        JsonObject ret = super.toJson().getAsJsonObject();
        ret.addProperty("blocks", getNumOfBlocks());
        return ret;
    }
}