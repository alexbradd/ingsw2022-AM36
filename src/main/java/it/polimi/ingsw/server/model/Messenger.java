package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

/**
 * Represents the Messenger card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Messenger extends Character {
    /**
     * Creates a new Messenger card.
     */
    Messenger() {
        super(Characters.MESSENGER, 1);
    }

    /**
     * The current player can move MotherNature an extra 2 steps.
     *
     * @param phase      the {@link ActionPhase} the card's effect has been called from
     * @param parameters unused
     * @throws IllegalArgumentException           if {@code phase} is null
     * @throws InvalidCharacterParameterException never thrown
     */
    @Override
    void doEffect(ActionPhase phase, String[] parameters) throws InvalidCharacterParameterException {
        super.doEffect(phase, parameters);
        phase.requestMNMovementExtension(2);
    }
}
