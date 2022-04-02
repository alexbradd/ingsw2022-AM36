package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

/**
 * Represents the Knight card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Knight extends Character {
    /**
     * Creates a new Knight card.
     */
    Knight() {
        super(Characters.KNIGHT, 2);
    }

    /**
     * The current player will be given 2 extra points in the influence calculation. Parameters are unused.
     *
     * @param phase      the {@link ActionPhase} the card's effect has been called from
     * @param parameters unused
     * @throws IllegalArgumentException           if {@code phase} is null
     * @throws InvalidCharacterParameterException never thrown
     */
    @Override
    void doEffect(ActionPhase phase, String[] parameters) throws InvalidCharacterParameterException {
        super.doEffect(phase, parameters);
        Player p = phase.getCurrentPlayer();
        phase.setInfluenceCalculator(new ExtraPointsInfluenceDecorator(phase.getInfluenceCalculator(), p, 2));
    }
}
