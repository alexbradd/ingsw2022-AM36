package it.polimi.ingsw.server.model;

import java.util.function.Function;

/**
 * Represents a tuple of elements
 *
 * @param <T> generic type
 * @param <V> generic type
 * @author Alexandru Gabriel Bradatan, Leonardo Bianconi, Mattia Busso
 */
class Tuple<T, V> {
    /**
     * The first element stored in the Tuple
     */
    private final T first;
    /**
     * The second element stored in the Tuple
     */
    private final V second;

    /**
     * Creates a new Tuple
     *
     * @param first  the first element to be stored in the Tuple
     * @param second the second element to be stored in the Tuple
     */
    Tuple(T first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element stored in the Tuple.
     *
     * @return the first element stored in the Tuple
     */
    T getFirst() {
        return first;
    }

    /**
     * Returns the second element stored in the Tuple.
     *
     * @return the second element stored in the Tuple
     */
    V getSecond() {
        return second;
    }

    /**
     * Applies the given mapping function to the tuple and returns the result.
     *
     * @param mapper the mapping function
     * @param <U>    return type of the mapper
     * @return result of the mapping function
     * @throws IllegalArgumentException if {@code mapper} is null
     */
    <U> U map(Function<Tuple<T, V>, U> mapper) {
        return mapper.apply(this);
    }

}