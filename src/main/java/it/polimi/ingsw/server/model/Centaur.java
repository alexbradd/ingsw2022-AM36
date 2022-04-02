package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

/**
 * Represents the Centaur character card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Centaur extends Character {
    /**
     * Creates a new Centaur card.
     */
    Centaur() {
        super(Characters.CENTAUR, 3);
    }

    /**
     * Ignore the additional influence of towers during influence calculation. Parameters are unused.
     *
     * @param phase      the {@link ActionPhase} the card's effect has been called from
     * @param parameters unused
     * @throws IllegalArgumentException           if {@code phase} is null
     * @throws InvalidCharacterParameterException never thrown
     */
    @Override
    void doEffect(ActionPhase phase, String[] parameters) throws InvalidCharacterParameterException {
        super.doEffect(phase, parameters);
        phase.setInfluenceCalculator(new IgnoreTowersInfluenceDecorator(phase.getInfluenceCalculator()));
    }
}
