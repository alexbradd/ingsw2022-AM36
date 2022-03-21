package it.polimi.ingsw.server.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * An object that calculates the influence of players on a given {@link Island} using the standard rule-set.
 *
 * @author Alexandru Gabriel Bradatan
 * @see Island
 */
class StandardInfluenceCalculator implements InfluenceCalculator {
    /**
     * Given an {@link Island}, calculates the influences of all players that have some. An {@link Optional} is
     * returned containing a {@link Map} that maps each player to its non-zero influence. The {@link Optional} is empty
     * if the island is blocked. This method does not account for potential groups the {@link Island} might be in.
     *
     * @param island the {@link Island} on which to perform the calculation
     * @return an {@link Optional} containing a {@link Map} that maps each {@link Player} to its influence
     * @throws IllegalArgumentException if {@code island} is null
     */
    @Override
    public Optional<Map<Player, Integer>> calculateInfluences(Island island) {
        if (island == null) throw new IllegalArgumentException("island shouldn't be null");
        if (island.isBlocked()) return Optional.empty();

        Map<Player, Integer> inf = new HashMap<>();
        island.getStudents().forEach(
                s -> s.getProfessor().getOwner().ifPresent((p) -> inf.merge(p, 1, Integer::sum)));
        island.getControllingPlayer().ifPresent(p -> inf.merge(p, island.getNumOfTowers(), Integer::sum));
        return Optional.of(inf);
    }
}
