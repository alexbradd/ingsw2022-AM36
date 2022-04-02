package it.polimi.ingsw.server.model;

/**
 * Represents a block card. These type of cards are a placeholder that signal to the influence calculation mechanism
 * that an island shouldn't have its influence calculated.
 *
 * @author Alexabdru Gabriel Bradatan
 */
class BlockCard {
    /**
     * The owner of this card
     */
    private final Character owner;

    /**
     * Creates a new BlockCard belonging to the given {@link Character}.
     *
     * @param owner the owner of this BlockCard
     * @throws IllegalArgumentException if {@code owner} is null
     */
    BlockCard(Character owner) {
        if (owner == null) throw new IllegalArgumentException("owner shouldn't be null");
        this.owner = owner;
    }

    /**
     * Getter for this BlockCard's owner
     *
     * @return this BlockCard's owner
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * Returns this card to its owner, if he has not already got it.
     */
    void returnToOwner() {
        if (!owner.containsBlock(this))
            owner.pushBlock(this);
    }
}
