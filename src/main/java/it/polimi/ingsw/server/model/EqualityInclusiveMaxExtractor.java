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
class EqualityInclusiveMaxExtractor extends MaxExtractor {
    /**
     * The player that is privileged in the maximum calculation.
     */
    private final Player privileged;

    /**
     * Constructs an EqualityMaxExtractor with the given {@code Player} bias.
     *
     * @param privileged the {@code Player} to favour in case multiple maximum candidates are found
     * @throws IllegalArgumentException if {@code privileged} is null
     */
    protected EqualityInclusiveMaxExtractor(Player privileged) {
        if (privileged == null) throw new IllegalArgumentException("privileged shouldn't be null");
        this.privileged = privileged;
    }

    /**
     * Returns true if the privileged player is between the maximum candidates.
     *
     * @param map          the {@link Map} to analyze
     * @param maxCandidate one of the maximum candidates
     * @return true if the privileged player is between the maximum candidates
     * @throws IllegalArgumentException if any of the parameters are null
     */
    private boolean isPrivilegedAMaxCandidate(Map<Player, Integer> map, Map.Entry<Player, Integer> maxCandidate) {
        if (map == null) throw new IllegalArgumentException("map shouldn't be null");
        if (maxCandidate == null) throw new IllegalArgumentException("maxCandidate shouldn't be null");
        return map.entrySet().stream()
                .filter((e) -> e.getValue().equals(maxCandidate.getValue()))
                .anyMatch((e) -> e.getKey().equals(privileged));
    }

    /**
     * Tries to determine the {@link Player} corresponding to the maximum value in {@link Map}. If it can be
     * determined, it is returned inside an {@link Optional}, else an empty {@link Optional} is returned. The criteria
     * followed is the following:
     * 1. If only one candidate is found, that is returned;
     * 2. If multiple candidates are found and {@code privileged} is between them, {@code privileged} is returned;
     * 3. If multiple candidates are found and {@code privileged} is NOT between them, an empty optional is returned.
     * 4. If the given map is empty, an empty optional is returned.
     *
     * @param map the {@link Map} to be analyzed
     * @return an {@link Optional} containing the maximum, if it can be determined
     * @throws IllegalArgumentException if {@code map} is null
     */
    @Override
    public Optional<Player> apply(Map<Player, Integer> map) {
        if (map == null) throw new IllegalArgumentException("map shouldn't be null");
        Optional<Map.Entry<Player, Integer>> opt_candidate = getMaximumCandidate(map);
        return opt_candidate.map(candidate -> {
            if (isValueUnique(map, candidate))
                return candidate.getKey();
            if (isPrivilegedAMaxCandidate(map, candidate))
                return privileged;
            return null;
        });
    }
}
