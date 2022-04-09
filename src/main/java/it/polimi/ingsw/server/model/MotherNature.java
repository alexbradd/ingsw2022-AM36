package it.polimi.ingsw.server.model;

import java.util.List;

/**
 * Represents the Mother Nature (MN) piece. MN iterates cyclically on groups of a list of {@link Island} using a
 * {@link IslandListIterator}.
 */
class MotherNature {
    /**
     * The iterator on a list of {@link Island}
     */
    private IslandListIterator iterator;

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
        iterator = null;
        current = list.get(startingPosition);
    }

    /**
     * Return the id of the {@link Island} Mother Nature is standing on
     * @return the id of the {@link Island} Mother Nature is standing on
     */
    int getCurrentIslandId() {
        return current.getId();
    }

    /**
     * Executes a movement of the given number of steps. The number of steps should be greater or equal than 1.
     *
     * @param list  the list on which to move
     * @param steps the number steps to take
     * @throws IllegalArgumentException if {@code list} is null
     * @throws IllegalArgumentException if {@code steps} is less than 1
     */
    void move(List<Island> list, int steps) {
        if (list == null) throw new IllegalArgumentException("list shouldn't be null");
        if (steps < 1) throw new IllegalArgumentException("Mother nature moves at least one step");
        iterator = new IslandListIterator(list, current);
        for (int i = 0; i < steps; i++) current = iterator.next();
    }
}
