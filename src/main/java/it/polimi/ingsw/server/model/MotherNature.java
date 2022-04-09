package it.polimi.ingsw.server.model;

import java.util.List;

/**
 * Represents the Mother Nature (MN) piece. MN iterates cyclically on a list of {@link Island} using a
 * {@link IslandListIterator}.
 */
class MotherNature {
    /**
     * The {@link Island} on which Mother Nature is currently on.
     */
    private Island current;

    /**
     * Creates a new Mother Nature that will move on the given list
     *
     * @param list             the list to move on
     * @param startingPosition the starting position
     * @throws IllegalArgumentException if {@code list} is null
     */
    MotherNature(List<Island> list, int startingPosition) {
        if (list == null) throw new IllegalArgumentException("list cannot be null");
        if (startingPosition < 0 || startingPosition >= list.size())
            throw new IllegalArgumentException("starting position out of bounds");
        current = list.get(startingPosition);
    }

    /**
     * Creates a new MotherNature that is a shallow copy of the given one.
     *
     * @param old the MotherNature to copy
     */
    MotherNature(MotherNature old) {
        if (old == null) throw new IllegalArgumentException("old cannot be null");
        this.current = old.current;
    }

    /**
     * Return the first id of the {@link Island} Mother Nature is standing on
     *
     * @return the id of the {@link Island} Mother Nature is standing on
     */
    int getCurrentIslandId() {
        return current.getIds().get(0);
    }

    /**
     * Return the Island that MotherNature is currently on
     *
     * @return the Island that MotherNature is currently on
     */
    Island getCurrentIsland() {
        return current;
    }

    /**
     * Executes a movement of the given number of steps. The number of steps should be greater or equal than 1.
     * MotherNature, in case the given doesn't contain the current Island, will try to find a new Island with at least
     * the same ids as the current one to start from.
     *
     * @param list  the list on which to move
     * @param steps the number steps to take
     * @throws IllegalArgumentException if {@code list} is null
     * @throws IllegalArgumentException if {@code steps} is less than 1
     * @throws IllegalArgumentException if {@code list} doesn't contain any compatible Island
     */
    MotherNature move(List<Island> list, int steps) {
        if (list == null) throw new IllegalArgumentException("list shouldn't be null");
        if (steps < 1) throw new IllegalArgumentException("Mother nature moves at least one step");
        MotherNature ret = new MotherNature(this);
        if (!list.contains(current)) { // the island got merged
            ret.current = list.stream()
                    .filter(i -> i.getIds().containsAll(current.getIds()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Could not find the old current island in the list"));
        }
        IslandListIterator iterator = new IslandListIterator(list, ret.current);
        for (int i = 0; i < steps; i++)
            ret.current = iterator.next();
        return ret;
    }
}
