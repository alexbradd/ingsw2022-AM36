package it.polimi.ingsw.server.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a decorator for {@link InfluenceCalculator} that removes the extra influence that towers give to the
 * player that placed them.
 *
 * @author Alexandru Gabriel Bradatan
 */
class IgnoreTowersInfluenceDecorator extends InfluenceCalculatorDecorator {
    /**
     * Creates a new decorator for the given {@link InfluenceCalculator}.
     *
     * @param toDecorate the {@link InfluenceCalculator} to decorate
     * @throws IllegalArgumentException if {@code toDecorate} is null
     */
    IgnoreTowersInfluenceDecorator(InfluenceCalculator toDecorate) {
        super(toDecorate);
    }

    /**
     * Given an {@link Island}, calculates the influences of all players that have some removing the extra influence
     * that towers give to the player that placed them. An {@link Optional} is returned containing a {@link Map} that
     * maps each player to its non-zero influence. The {@link Optional} is empty if the island is blocked.
     *
     * @param island the {@link Island} on which to perform the calculation
     * @return an {@link Optional} containing a {@link Map} that maps each {@link Player} to its influence
     * @throws IllegalArgumentException if {@code island} is null
     */
    @Override
    public Optional<Map<Player, Integer>> calculateInfluences(Island island, List<Professor> professors) {
        if (island == null) throw new IllegalArgumentException("island shouldn't be null");
        InfluenceCalculator decorated = getCalculator();
        return decorated.calculateInfluences(island, professors)
                .map(inf -> {
                    island.getControllingPlayer()
                            .ifPresent(p -> inf.computeIfPresent(p, (k, i) -> {
                                int corrected = i - island.getNumOfTowers();
                                if (corrected > 0) return corrected;
                                return null;
                            }));
                    return inf;
                });

    }
}
