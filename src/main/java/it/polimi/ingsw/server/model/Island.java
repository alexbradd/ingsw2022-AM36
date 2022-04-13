package it.polimi.ingsw.server.model;

import java.util.*;

/**
 * Represents an island. Each island has a unique, game-local id and can be bounded to an {@link IslandList}. An Island
 * can receive students, be conquered by a {@link Player} by placing {@link Tower}s, and be blocked by a
 * {@link BlockCard}. Islands, if conquered by the same player, can be "merged": one island will become the "parent" and
 * hold all the state of its children. The children will redirect all state changes to the parent. One exception to this
 * rule are towers: each island will always have its tower and conquering a "group" of islands will place a tower on
 * each island in the group.
 *
 * @author Alexandru Bradatan Gabriel
 * @see IslandList
 */
class Island implements StudentMoveDestination {
    /**
     * The island's ID.
     */
    private final int id;

    /**
     * The island's set of children.
     */
    private final Set<Island> children;

    /**
     * The island's parent.
     */
    private Island parent;

    /**
     * The set of Students located on the current Island
     */
    private final HashSet<Student> students;

    /**
     * The Tower placed on this Island
     */
    private Tower tower;

    /**
     * The Stack of BlockCards placed on the island
     */
    private final Stack<BlockCard> blockCards;

    /**
     * The list to which this island is bound
     */
    private final IslandList bound;

    /**
     * Creates a new unbound empty island with the given ID.
     */
    Island(int id) {
        this(id, null);
    }

    /**
     * Creates a new empty island with the given ID bounded to the given {@link IslandList}. Is null is passed as bound,
     * the Island will be unbounded.
     */
    Island(int id, IslandList bound) {
        this.id = id;
        students = new HashSet<>();
        children = new HashSet<>();
        parent = null;
        blockCards = new Stack<>();
        this.bound = bound;
    }

    /**
     * Getter for the island id.
     *
     * @return the island id
     */
    int getId() {
        return id;
    }

    /**
     * Return an {@link Optional} containing the parent island. If there is none, an empty {@link Optional} is returned.
     *
     * @return an optional containing the parent island
     */
    Optional<Island> getParent() {
        return Optional.ofNullable(parent);
    }

    /**
     * Getter for the {@link Set} of students located on the island.
     *
     * @return a copy of the set of students located on the island
     * @see Student
     */
    HashSet<Student> getStudents() {
        return new HashSet<>(getParent().orElse(this).students);
    }

    /**
     * Returns a {@link Optional} containing the player that has control over this island. If there isn't such player
     * an empty {@link Optional} is returned.
     *
     * @return a {@link Optional} containing the player that has control over this island, if present.
     */
    Optional<Player> getControllingPlayer() {
        if (tower == null) return Optional.empty();
        return Optional.of(tower.getOwner());
    }

    /**
     * Add the given {@link Student} to the internal store.
     *
     * @param student the {@link Student} to add to the store
     * @throws IllegalArgumentException if {@code student} is null
     */
    @Override
    public void receiveStudent(Student student) {
        if (student == null) throw new IllegalArgumentException("student shouldn't be null");
        getParent().orElse(this).students.add(student);
    }

    /**
     * Returns true if receiving a {@link Student} modifies the {@link Professor} assignments. For islands, this will
     * always return false.
     *
     * @return true if receiving a {@link Student} modifies the {@link Professor} assignments, false otherwise
     * @see Professor
     */
    @Override
    public boolean requiresProfessorAssignment() {
        return false;
    }

    /**
     * Makes this island conquered by the given {@link Player}.
     * <p>
     * Small note: a conquered group of islands not always has towers on all of his members. This may happen during
     * endgame. We preferred not to bubble-up the exception and instead check al winning conditions during the
     * respective phases.
     *
     * @param player the {@link Player} that conquers this island
     * @throws IllegalArgumentException if {@code player} is null
     */
    void conquer(Player player) {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (getControllingPlayer().equals(Optional.of(player)))
            return;
        Island toConquer = getParent().orElse(this);
        toConquer.receiveTowerFrom(player);
        toConquer.children.forEach(i -> i.receiveTowerFrom(player));
        if (bound != null) bound.scrub();
    }

    /**
     * Receives a {@link Tower} from the given player
     *
     * @param player player from whom to receive the tower
     * @throws IllegalArgumentException if {@code player} is null
     */
    private void receiveTowerFrom(Player player) {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (tower != null) tower.getOwner().receiveTower(tower);
        try {
            player.sendTower((t) -> this.tower = t);
        } catch (IllegalStateException ignored) {
            // It is safe to ignore this exception since a group that has only some towers of some player is a valid
            // game state.
        }
    }

    /**
     * Returns the number of towers placed on this island. If the island is in a group, it returns the number of towers
     * placed on the group.
     *
     * @return the number of towers placed on this island
     */
    int getNumOfTowers() {
        Island parent = getParent().orElse(this);
        return parent.getControllingPlayer()
                .map(p -> (int) parent.children.stream().filter(Island::hasTower).count() + 1)
                .orElse(0);
    }

    /**
     * Returns true if this island has an island placed on it.
     *
     * @return true if this island has an island placed on it.
     */
    private boolean hasTower() {
        return tower != null;
    }

    /**
     * Merges the given Island into this one. If the two Islands re related, nothing is done. If the two Islands
     * aren't compatible (as returned by {@code canBeMergedWith()}) an exception is thrown.
     *
     * @param other Island to merge with
     * @throws IllegalArgumentException if {@code other} is null
     * @throws IllegalArgumentException if {@code other} isn't compatible
     */
    void merge(Island other) {
        if (other == null) throw new IllegalArgumentException("other shouldn't be null");
        if (!canBeMergedWith(other)) throw new IllegalArgumentException("other cannot be merged");
        if (isRelatedTo(other)) return;

        Island parent = getParent().orElse(this);
        Island toMerge = other.getParent().orElse(other);
        toMerge.transferBlocks(parent);
        parent.addChildren(toMerge);
        toMerge.transferChildren(parent);
        toMerge.transferStudents(parent);
    }

    /**
     * Returns true if this Island can be merged with the given one.
     *
     * @param other Island to check
     * @return true if this Island can be merged with the given one.
     * @throws IllegalArgumentException if {@code other} is null
     */
    boolean canBeMergedWith(Island other) {
        if (other == null) throw new IllegalArgumentException("other shouldn't be null");
        return this.getControllingPlayer()
                .map((p) -> other.getControllingPlayer()
                        .filter(p::equals).isPresent())
                .orElse(false);
    }

    /**
     * Returns true if this island is related to the given one. Two Islands are related if:
     * <p>
     * 1. They are equal;
     * 2. One is the child of the other;
     * 3. The islands are siblings.
     *
     * @param other Island to check
     * @return true if this island is related to the given one
     * @throws IllegalArgumentException if {@code other} is null
     */
    boolean isRelatedTo(Island other) {
        if (other == null) throw new IllegalArgumentException("other shouldn't be null");
        return equals(other) || // equals
                getParent().map(other::equals).orElse(false) || // parent
                getParent().map(p -> other.getParent().filter(p::equals).isPresent()).orElse(false) || //sibling
                children.contains(other); //child
    }

    /**
     * Transfers blocks, if present, on the given Island.
     *
     * @param receiver Island that will receive the blocks
     * @throws IllegalArgumentException if {@code receiver} is null
     */
    private void transferBlocks(Island receiver) {
        if (receiver == null) throw new IllegalArgumentException("receiver shouldn't be null");
        for (BlockCard b : blockCards)
            receiver.pushBlock(b);
        blockCards.clear();
    }

    /**
     * Adds the given Island to the children set.
     *
     * @param child the children to add
     * @throws IllegalArgumentException if {@code child} is null
     */
    private void addChildren(Island child) {
        if (child == null) throw new IllegalArgumentException("child shouldn't be null");
        children.add(child);
        child.parent = this;
    }

    /**
     * Transfers all children Islands to another Island.
     *
     * @param receiver the receiver of the children
     * @throws IllegalArgumentException if {@code receiver} is null
     */
    private void transferChildren(Island receiver) {
        if (receiver == null) throw new IllegalArgumentException("receiver shouldn't be null");
        children.forEach(receiver::addChildren);
        children.clear();
    }

    /**
     * Transfers all students to another Island.
     *
     * @param receiver the receiver of the students
     * @throws IllegalArgumentException if {@code receiver} is null
     */
    private void transferStudents(Island receiver) {
        if (receiver == null) throw new IllegalArgumentException("receiver shouldn't be null");
        students.forEach(receiver::receiveStudent);
        students.clear();
    }

    /**
     * Return true if the island is blocked by at least one {@link BlockCard}.
     *
     * @return true if the island is blocked by a {@link BlockCard}, false otherwise
     */
    boolean isBlocked() {
        return !getParent().orElse(this).blockCards.isEmpty();
    }

    /**
     * Push a block on the island.
     *
     * @param block the {@link BlockCard} to place
     * @throws IllegalArgumentException if {@code block} is null
     */
    void pushBlock(BlockCard block) {
        if (block == null) throw new IllegalArgumentException("block shouldn't be null");
        Island toBlock = getParent().orElse(this);
        toBlock.blockCards.push(block);
    }

    /**
     * Pop one block from the island, and return it to its owner.
     *
     * @throws IllegalStateException if the entity isn't blocked
     */
    void popBlock() {
        Island toUnblock = getParent().orElse(this);
        if (!toUnblock.isBlocked()) throw new IllegalStateException("cannot unblock an already unblocked island");
        toUnblock.blockCards.pop().returnToOwner();
    }

    /**
     * Indicates whether some other object is "equal to" this one. Two islands are equals only if they have the same ID
     * or are the same instance.
     *
     * @param o the object to compare
     * @return true if the given object is equal to this one
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Island island = (Island) o;
        return id == island.id;
    }

    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for the object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this island.
     *
     * @return a string representation of this island
     */
    @Override
    public String toString() {
        return "Island{" +
                "id=" + id +
                ", children=" + children +
                ", parent=" + getParent().map(Island::superToString).orElse(null) +
                ", students=" + students +
                ", tower=" + tower +
                ", block=" + blockCards +
                '}';
    }

    /**
     * Returns the string representation of the superclass (object in this case)
     *
     * @return the string representation of the superclass (object in this case)
     */
    private String superToString() {
        return super.toString();
    }
}
