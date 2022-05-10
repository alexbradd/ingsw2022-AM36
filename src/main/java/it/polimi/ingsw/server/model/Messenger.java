package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

/**
 * Represents the Messenger card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Messenger extends Character {
    /**
     * Base constructor. Sets up only the card's initial cost and character
     */
    Messenger() {
        super(CharacterType.MESSENGER);
    }

    /**
     * Returns a copy of the passed Character
     *
     * @param old Character to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private Messenger(Character old) {
        super(old);
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    Character shallowCopy() {
        return new Messenger(this);
    }

    /**
     * The current player can move MotherNature an extra 2 steps. This card doesn't require any steps.
     *
     * @param phase the {@link ActionPhase} the card's effect has been called from
     * @param steps an array of {@link CharacterStep} configuring the effect
     * @return a Tuple containing the updated ActionPhase and the updated Character
     * @throws IllegalArgumentException           if {@code phase} or {@code steps} or are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code steps} is formatted incorrectly
     */
    @Override
    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep[] steps) throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        return super.doEffect(phase, steps)
                .map(t -> new Tuple<>(
                        t.getFirst().requestExtraMnMovement(2),
                        t.getSecond()));
    }
}
