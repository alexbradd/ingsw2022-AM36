package it.polimi.ingsw.server.model;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Represents a function that tries to extract the {@link Player} associated with the maximum value in the given
 * {@link Map}. If said {@link Player} can be found, an {@link Optional} containing it is returned, otherwise an empty
 * {@link Optional} is returned. For more details on the edge cases, see the detailed documentation of the {@code apply}
 * method.
 *
 * @author Alexandru Gabriel Bradatan
 */
abstract class MaxExtractor implements Function<Map<Player, Integer>, Optional<Player>> {

    /**
     * Returns a {@link Map.Entry} from the given {@link Map} containing a possible maximum candidate and the relative
     * value.
     *
     * @param map the {@link Map} to search
     * @return a {@link Map.Entry} that contains the maximum value in {@code map}
     * @throws IllegalArgumentException if {@code map} is null
     */
    protected Optional<Map.Entry<Player, Integer>> getMaximumCandidate(Map<Player, Integer> map) {
        if (map == null) throw new IllegalArgumentException("map shouldn't be null");
        return map.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue));

    }

    /**
     * Returns {@code true} if the given {@link Map.Entry}'s value is unique inside the given {@link Map}, false
     * otherwise.
     *
     * @param map   the {@link Map} to search
     * @param entry the {@link Map.Entry} to check for uniqueness
     * @return true if {@code entry}'s value is unique in {@code map}, else false
     * @throws IllegalArgumentException if {@code map} or {@code entry} is null
     */
    protected boolean isValueUnique(Map<Player, Integer> map, Map.Entry<Player, Integer> entry) {
        if (map == null) throw new IllegalArgumentException("map shouldn't be null");
        if (entry == null) throw new IllegalArgumentException("entry shouldn't be null");
        return map.entrySet().stream()
                .noneMatch((e) -> !entry.getKey().equals(e.getKey()) && entry.getValue().equals(e.getValue()));
    }

    /**
     * Tries to determine the {@link Player} corresponding to the maximum value in {@link Map}. If it can be
     * determined, it is returned inside an {@link Optional}, else an empty {@link Optional} is returned.
     *
     * @param map the {@link Map} to be analyzed
     * @return an {@link Optional} containing the maximum, if it can be determined
     * @throws IllegalArgumentException if {@code map} is null
     */
    public abstract Optional<Player> apply(Map<Player, Integer> map);
}
