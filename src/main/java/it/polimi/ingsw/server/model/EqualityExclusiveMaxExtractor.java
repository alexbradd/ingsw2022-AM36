package it.polimi.ingsw.server.model;

import java.util.Map;
import java.util.Optional;

/**
 * Represents a function that tries to extract the {@link Player} associated with the maximum value in the given
 * {@link Map}. If said {@link Player} can be found, an {@link Optional} containing it is returned, otherwise an empty
 * {@link Optional} is returned. For more details on the edge cases, see the detailed documentation of the {@code apply}
 * method.
 *
 * @author Alexandru Gabriel Bradatan
 */
public class EqualityExclusiveMaxExtractor extends MaxExtractor {
    /**
     * Tries to determine the {@link Player} corresponding to the maximum value in {@link Map}. If it can be
     * determined, it is returned inside an {@link Optional}, else an empty {@link Optional} is returned. The criteria
     * followed is the following:
     * 1. If only one candidate is found, that is returned;
     * 2. If multiple candidates are found, an empty optional is returned.
     * 3. If the given map is empty, an empty optional is returned.
     *
     * @param map the {@link Map} to be analyzed
     * @return an {@link Optional} containing the maximum, if it can be determined
     * @throws IllegalArgumentException if {@code map} is null
     */
    @Override
    public Optional<Player> apply(Map<Player, Integer> map) throws IllegalArgumentException {
        if (map == null) throw new IllegalArgumentException("map shouldn't be null");
        return getMaximumCandidate(map)
                .filter(c -> isValueUnique(map, c))
                .map(Map.Entry::getKey);
    }
}
