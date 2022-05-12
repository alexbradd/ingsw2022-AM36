package it.polimi.ingsw.server.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.TowerColor;

import java.util.*;
import java.util.function.Function;

/**
 * Represents an island. Each Island has a set of game-local ids (used for the graphical rendition). An Island can
 * receive students, be conquered by a {@link Player} by placing {@link Tower}s, and be blocked by a {@link BlockCard}.
 * Islands, if conquered by the same player, can be merged into a new Island containing all the  sum of the state of the
 * "addends". The ids are stored in order, this means that merging [3,4] into [1,2] results into a new Island with
 * [1,2,3,4].
 * <p>
 * Island is completely immutable, every state change will generate a new Island.
 *
 * @author Alexandru Bradatan Gabriel
 */
class Island implements Jsonable {
    private List<Integer> ids;
    private StudentContainer container;
    private List<BlockCard> blocks;
    private List<Tower> towers;

    /**
     * Creates a new Island with the given id.
     */
    Island(int id) {
        ids = new ArrayList<>();
        ids.add(id);
        container = new StudentContainer();
        blocks = new ArrayList<>();
        towers = new ArrayList<>();
    }

    /**
     * Creates a new Island that is a shallow copy of the five one.
     *
     * @throws IllegalArgumentException if {@code old} is null
     */
    Island(Island old) {
        if (old == null) throw new IllegalArgumentException("old cannot be null");
        ids = old.ids;
        container = old.container;
        blocks = old.blocks;
        towers = old.towers;
    }

    /**
     * Returns a copy of the ids of this Island
     *
     * @return a {@link List} containing this Island's ids
     */
    public List<Integer> getIds() {
        return new ArrayList<>(ids);
    }

    /**
     * Returns an {@link Optional} containing the {@link Player} currently controlling this Island.
     *
     * @return an {@link Optional} containing the {@link Player} currently controlling this Island
     */
    Optional<Player> getControllingPlayer() {
        if (towers.isEmpty())
            return Optional.empty();
        return Optional.of(towers.get(0).getOwner());
    }

    /**
     * Returns an {@link Optional} containing the {@link TowerColor} of the towers currently placed on this island.
     *
     * @return an {@link Optional} containing the {@link TowerColor} of the towers currently placed on this island
     */
    Optional<TowerColor> getConqueringColor() {
        if (towers.isEmpty())
            return Optional.empty();
        return Optional.of(towers.get(0).getColor());
    }

    /**
     * Returns a copy of the set of all students placed on this island.
     *
     * @return a copy of the set of all students placed on this island
     */
    Set<Student> getStudents() {
        return container.getStudents();
    }

    /**
     * Returns the number of currently placed towers.
     *
     * @return the number of currently placed towers.
     */
    int getNumOfTowers() {
        return towers.size();
    }

    /**
     * Returns the maximum number of towers placeable on this Island. It is equal to the number of ids this Island has.
     *
     * @return the maximum number of towers placeable on this Island.
     */
    int getMaxNumOfTowers() {
        return ids.size();
    }

    /**
     * Returns a copy of the list of Tower stored on the current island
     *
     * @return a copy of the list of Tower stored on the current island
     */
    List<Tower> getTowers() {
        return new ArrayList<>(towers);
    }

    /**
     * Applies the given update to the set of students placed on this Island. If the update returns null, nothing is
     * done.
     *
     * @param update a {@link Function} transforming the old {@link StudentContainer} into the new one
     * @return a new Island with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     * @see StudentContainer
     */
    Island updateStudents(Function<StudentContainer, StudentContainer> update) {
        if (update == null) throw new IllegalArgumentException("update shouldn't be null");
        StudentContainer newContainer = update.apply(new StudentContainer(this.container));
        Island newIsland = new Island(this);
        if (newContainer != null)
            newIsland.container = newContainer;
        return newIsland;
    }

    /**
     * Applies the given update to the towers placed on the current Island. The list of towers should have towers all
     * the same color, mismatch is not allowed.
     *
     * @param update a {@link Function} transforming the old set of towers in the new one
     * @return a new Island with the update applied
     * @throws IllegalArgumentException if any argument is null
     * @throws IllegalArgumentException if the list returned by {@code update} contains mismatched towers or too many
     *                                  towers
     */
    Island updateTowers(Function<List<Tower>, List<Tower>> update) {
        if (update == null) throw new IllegalArgumentException("update shouldn't be null");

        Island newIsland = new Island(this);
        List<Tower> newList = update.apply(new ArrayList<>(this.towers));
        if (newList != null) {
            if (!homogeneousList(newList))
                throw new IllegalArgumentException("Cannot apply update with mismatch towers");
            if (newList.size() > getMaxNumOfTowers())
                throw new IllegalArgumentException("Cannot apply update with this amount of towers");
            newIsland.towers = newList;
        }
        return newIsland;
    }

    /**
     * Private helper that checks if a list has towers all the same color.
     *
     * @param list the list tho check
     * @return true if the given list has towers all the same color
     */
    private boolean homogeneousList(List<Tower> list) {
        if (list.isEmpty())
            return true;
        TowerColor c = list.get(0).getColor();
        for (Tower t : list)
            if (!t.getColor().equals(c))
                return false;
        return true;
    }

    /**
     * Merge the given Island into the current Island to return a new Island with merged state.
     *
     * @param toMerge the Island to merge
     * @return a new Island
     * @throws IllegalArgumentException if {@code toMerge} is null or cannot be merged into the current island
     */
    Island merge(Island toMerge) {
        if (toMerge == null) throw new IllegalArgumentException("toMerge cannot be null");
        if (!canBeMergedWith(toMerge)) throw new IllegalArgumentException("toMerge cannot be merged");

        Island newIsland = new Island(this);
        newIsland.ids = mergeLists(newIsland.ids, toMerge.ids);
        newIsland.towers = mergeLists(newIsland.towers, toMerge.towers);
        newIsland.blocks = mergeLists(newIsland.blocks, toMerge.blocks);
        newIsland.container = mergeContainers(newIsland.container, toMerge.container);
        return newIsland;
    }

    /**
     * Private helper that merges {@code merger} into {@code original}
     *
     * @param original First list
     * @param merger   List that will be merged into {@code original}
     * @param <T>      generic type
     * @return a new list that is the result of the merge
     */
    private <T> List<T> mergeLists(List<T> original, List<T> merger) {
        List<T> l = new ArrayList<>(original);
        l.addAll(merger);
        return l;
    }

    /**
     * Private helper that merges {@code merger} into {@code original}
     *
     * @param original First {@link StudentContainer}
     * @param merger   {@link StudentContainer} that will be merged into {@code original}
     * @return a new {@link StudentContainer} that is the result of the merge
     */
    private StudentContainer mergeContainers(StudentContainer original, StudentContainer merger) {
        StudentContainer c = new StudentContainer(original);
        for (Student s : merger.getStudents())
            c = c.add(s);
        return c;
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

        return this.getConqueringColor()
                .map(c -> other.getConqueringColor().filter(c::equals).isPresent())
                .orElse(false);
    }

    /**
     * Return true if the island is blocked by at least one {@link BlockCard}.
     *
     * @return true if the island is blocked by a {@link BlockCard}, false otherwise
     */
    boolean isBlocked() {
        return blocks.size() != 0;
    }

    /**
     * Returns the number of blocks currently placed on this island.
     *
     * @return the number of blocks currently placed on this island
     */
    int getNumOfBlocks() {
        return blocks.size();
    }

    /**
     * Push a block on the Island.
     *
     * @param block the {@link BlockCard} to place
     * @return a new updated Island
     * @throws IllegalArgumentException if {@code block} is null
     */
    Island pushBlock(BlockCard block) {
        if (block == null) throw new IllegalArgumentException("block shouldn't be null");
        Island newIsland = new Island(this);
        newIsland.blocks = new ArrayList<>(this.blocks);
        newIsland.blocks.add(block);
        return newIsland;
    }

    /**
     * Pop one block from the island.
     *
     * @return a {@link Tuple} containing the new updated Island and the popped block in order.
     * @throws IllegalStateException if the Island isn't blocked
     */
    Tuple<Island, BlockCard> popBlock() {
        if (!isBlocked()) throw new IllegalStateException("cannot unblock an unblocked card");
        Island newIsland = new Island(this);
        newIsland.blocks = new ArrayList<>(this.blocks);
        return new Tuple<>(newIsland, newIsland.blocks.remove(0));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Island island = (Island) o;
        return ids.equals(island.ids) &&
                Objects.equals(container, island.container) &&
                blocks.size() == island.blocks.size() &&
                towers.size() == island.towers.size() &&
                Objects.equals(getConqueringColor(), island.getConqueringColor());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(ids);
    }

    /**
     * {@inheritDoc}
     */
    public JsonElement toJson() {
        JsonObject ret = new JsonObject();

        JsonArray ids = new JsonArray();
        getIds().forEach(ids::add);

        JsonArray students = new JsonArray();
        getStudents().forEach(s -> students.add(s.getColor().toString()));

        JsonArray towers = new JsonArray();
        getTowers().forEach(t -> towers.add(t.getColor().toString()));

        ret.add("ids", ids);
        ret.add("students", students);
        ret.add("towers", towers);
        ret.addProperty("blocks", getNumOfBlocks());

        return ret;
    }
}