package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

/**
 * Represents the Innkeeper card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Innkeeper extends Character {
    /**
     * Base constructor.
     */
    public Innkeeper() {
        super(CharacterType.INNKEEPER);
    }

    /**
     * Returns a copy of the passed Character
     *
     * @param old Character to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private Innkeeper(Character old) {
        super(old);
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    Character shallowCopy() {
        return new Innkeeper(this);
    }

    /**
     * The current player will take control of the professors even in case of equality with current holder. This card
     * does not require any steps.
     *
     * @param phase the {@link ActionPhase} the card's effect has been called from
     * @param steps an array of {@link CharacterStep} configuring the effect
     * @return a Tuple containing the updated ActionPhase and the updated Character
     * @throws IllegalArgumentException           if {@code phase} or {@code steps} or are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code steps} is formatted incorrectly
     */
    @Override
    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep... steps) throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        checkEffectParameters(phase, steps);
        return super.doEffect(phase, steps)
                .map(t -> {
                    Player p = phase.getCurrentPlayer();
                    ActionPhase newPhase = t.getFirst().setMaxExtractor(new EqualityInclusiveMaxExtractor(p));
                    return new Tuple<>(newPhase, t.getSecond());
                });
    }
}
