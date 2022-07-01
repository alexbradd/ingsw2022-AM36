package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

/**
 * Represents the Herald character card.
 *
 * @author Alexandru Gabriel Bradatan
 */
public class Herald extends Character {
    /**
     * Base constructor. Sets up only the card's initial cost and character
     */
    Herald() {
        super(CharacterType.HERALD);
    }

    /**
     * Returns a copy of the passed Character
     *
     * @param old Character to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private Herald(Character old) {
        super(old);
    }

    /**
     * Request an extra influence calculation on the given island. This card uses 1 step, any more will be ignored. The
     * parameters used in the step are the following:
     *
     * <ul>
     *     <li>island: island index on which the calculation should be performed</li>
     * </ul>
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
        int islandIndex = steps[0].getParameterAsIslandIndex("island", phase);
        return super.doEffect(phase, steps)
                .map(((actionPhase, character) -> new Tuple<>(actionPhase.assignTower(islandIndex), character)));
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    Character shallowCopy() {
        return new Herald(this);
    }
}
