package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

/**
 * Represents the Innkeeper card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Innkeeper extends Character {
    /**
     * Creates a new Innkeeper card.
     */
    Innkeeper() {
        super(Characters.INNKEEPER, 2);
    }

    /**
     * The current player will take control of the professors even inc ase of equality with current holder. Parameters
     * are unused.
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
        phase.setMaximumExtractor(new EqualityInclusiveMaxExtractor(p));
    }
}
