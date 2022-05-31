package it.polimi.ingsw.functional;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a tuple of elements
 *
 * @param <T> generic type
 * @param <V> generic type
 * @author Alexandru Gabriel Bradatan, Leonardo Bianconi, Mattia Busso
 */
public class Tuple<T, V> {
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
    public Tuple(T first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first element stored in the Tuple.
     *
     * @return the first element stored in the Tuple
     */
    public T getFirst() {
        return first;
    }

    /**
     * Returns the second element stored in the Tuple.
     *
     * @return the second element stored in the Tuple
     */
    public V getSecond() {
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
    public <U> U map(Function<Tuple<T, V>, U> mapper) {
        if (mapper == null) throw new IllegalArgumentException("mapper shouldn't be null");
        return mapper.apply(this);
    }

    /**
     * Equivalent to {@link #map(Function)}, only that it takes a {@link BiFunction}.
     *
     * @param mapper the mapping function
     * @param <U>    return type of the mapper
     * @return result of the mapping function
     * @throws IllegalArgumentException if {@code mapper} is null
     */
    public <U> U map(BiFunction<T, V, U> mapper) {
        if (mapper == null) throw new IllegalArgumentException("mapper shouldn't be null");
        return mapper.apply(this.getFirst(), this.getSecond());
    }

    public void consume(BiConsumer<T, V> consumer) {
        if (consumer == null) throw new IllegalArgumentException("consumer shouldn't be null");
        consumer.accept(this.getFirst(), this.getSecond());
    }

    /**
     * Equivalent to {@link #map(Function)}, only that it takes a {@link ThrowingFunction}.
     *
     * @param mapper the mapping function
     * @param <U>    return type of the mapper
     * @param <E>    type of the exception thrown by the mapper
     * @return result of the mapping function
     * @throws IllegalArgumentException if {@code mapper} is null
     * @throws E                        exception thrown by {@code mapper}
     */
    public <U, E extends Exception> U throwMap(ThrowingFunction<Tuple<T, V>, U, E> mapper) throws E {
        if (mapper == null) throw new IllegalArgumentException("mapper shouldn't be null");
        return mapper.apply(this);
    }

    /**
     * Equivalent to {@link #map(BiFunction)}, only that it takes a {@link ThrowingBiFunction}.
     *
     * @param mapper the mapping function
     * @param <U>    return type of the mapper
     * @param <E>    type of the exception thrown by the mapper
     * @return result of the mapping function
     * @throws IllegalArgumentException if {@code mapper} is null
     * @throws E                        exception thrown by {@code mapper}
     */
    public <U, E extends Exception> U throwMap(ThrowingBiFunction<T, V, U, E> mapper) throws E {
        if (mapper == null) throw new IllegalArgumentException("mapper shouldn't be null");
        return mapper.apply(this.getFirst(), this.getSecond());
    }
}
