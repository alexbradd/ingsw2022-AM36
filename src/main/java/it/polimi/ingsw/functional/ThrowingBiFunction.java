package it.polimi.ingsw.functional;

import java.util.function.BiFunction;

/**
 * Similar to {@link BiFunction}, however the method can throw an exception
 *
 * @param <T> the type of the first argument
 * @param <U> the type of the second argument
 * @param <V> the type of the return value
 * @param <E> the type of the extension thrown
 */
@FunctionalInterface
public interface ThrowingBiFunction<T, U, V, E extends Exception> {
    /**
     * Applies this function to the given arguments
     *
     * @param first  the first argument
     * @param second the second argument
     * @return the function result
     * @throws E the exception thrown
     */
    V apply(T first, U second) throws E;
}
