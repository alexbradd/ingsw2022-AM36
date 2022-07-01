package it.polimi.ingsw.server.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a decorator for {@link InfluenceCalculator} that adds some extra points of influence to a given player.
 *
 * @author Alexandru Gabriel Bradatan
 */
public class ExtraPointsInfluenceDecorator extends InfluenceCalculatorDecorator {
    /**
     * The {@link Player} that will receive the extra amount of points.
     */
    private final Player favourite;

    /**
     * The amount of extra points that will be added.
     */
    private final int points;

    /**
     * Creates a new decorator for the given {@link InfluenceCalculator}.
     *
     * @param toDecorate the {@link InfluenceCalculator} to decorate
     * @param favourite  the {@link Player} to favour
     * @param points     how many points of influence should the favoured player receive
     * @throws IllegalArgumentException if {@code toDecorate} or {@code favourite} are null or if {@code points} is less
     *                                  than zero
     */
    public ExtraPointsInfluenceDecorator(InfluenceCalculator toDecorate, Player favourite, int points) {
        super(toDecorate);
        if (favourite == null) throw new IllegalArgumentException("favourite shouldn't be null");
        if (points < 0) throw new IllegalArgumentException("points should be a positive integer");
        this.favourite = favourite;
        this.points = points;
    }

    /**
     * Given an {@link Island}, calculates the influences of all players that have some. The {@link Player} given during
     * construction will receive the specified extra amount of points. An {@link Optional} is returned containing a
     * {@link Map} that maps each player to its non-zero influence. The {@link Optional} is empty if the island is
     * blocked.
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
                    inf.merge(favourite, points, Integer::sum);
                    return inf;
                });
    }
}
