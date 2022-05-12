package it.polimi.ingsw.functional;

import java.util.function.Function;

/**
 * Similar to {@link Function}, however the method can throw an exception
 *
 * @param <T> the type of the first argument
 * @param <V> the type of the return value
 * @param <E> the type of the extension thrown
 */
@FunctionalInterface
public interface ThrowingFunction<T, V, E extends Exception> {
    /**
     * Applies this function to the given arguments
     *
     * @param first the first argument
     * @return the function result
     * @throws E the exception thrown
     */
    V apply(T first) throws E;
}
