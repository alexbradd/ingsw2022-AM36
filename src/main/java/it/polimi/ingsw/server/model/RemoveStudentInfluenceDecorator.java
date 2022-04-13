package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.PieceColor;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a decorator for {@link InfluenceCalculator} that removes one student color from the influence calculation.
 *
 * @author Alexandru Gabriel Bradatan
 */
class RemoveStudentInfluenceDecorator extends InfluenceCalculatorDecorator {
    /**
     * The color of the students that will be excluded during influence calculation.
     */
    private final PieceColor colorToIgnore;

    /**
     * Creates a new decorator for the given {@link InfluenceCalculator}.
     *
     * @param toDecorate the {@link InfluenceCalculator} to decorate
     * @param color      the {@link PieceColor} to ignore
     * @throws IllegalArgumentException if {@code toDecorate} is null
     */
    public RemoveStudentInfluenceDecorator(InfluenceCalculator toDecorate, PieceColor color) {
        super(toDecorate);
        this.colorToIgnore = color;
    }

    /**
     * Given an {@link Island}, calculates the influences of all players that have some ignoring students of a certain
     * color from the calculation. An {@link Optional} is returned containing a {@link Map} that maps each player to its
     * non-zero influence. The {@link Optional} is empty if the island is blocked.
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
                    int correction = (int) island.getStudents().stream()
                            .filter(s -> s.getColor().equals(colorToIgnore))
                            .count();

                    Professor professor = professors.stream()
                            .filter(p -> p.getColor().equals(colorToIgnore))
                            .findFirst()
                            .orElseThrow();

                    Optional<Player> owner = professor.getOwner();

                    if (owner.isPresent()) {
                        inf.put(owner.get(), inf.get(owner.get()) - correction);
                        if (inf.get(owner.get()) <= 0)
                            inf.put(owner.get(), null);
                    }
                    return inf;
                });
    }
}
