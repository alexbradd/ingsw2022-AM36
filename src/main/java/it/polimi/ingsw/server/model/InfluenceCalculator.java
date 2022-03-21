package it.polimi.ingsw.server.model;

import java.util.Map;
import java.util.Optional;

/**
 * An object that calculates the influence of players on a given {@link Island}.
 *
 * @author Alexandru Gabriel Bradatan
 * @see Island
 */
interface InfluenceCalculator {
    /**
     * Given an {@link Island}, calculates the influences of all players that have some. An {@link Optional} is
     * returned containing a {@link Map} that maps each player to its non-zero influence. The {@link Optional} is empty
     * if the island is blocked.
     *
     * @param island the {@link Island} on which to perform the calculation
     * @return an {@link Optional} containing a {@link Map} that maps each {@link Player} to its influence
     * @throws IllegalArgumentException if {@code island} is null
     */
    Optional<Map<Player, Integer>> calculateInfluences(Island island);
}
