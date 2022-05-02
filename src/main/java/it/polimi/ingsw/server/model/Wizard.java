package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

/**
 * Represents the Wizard card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Wizard extends Character {
    /**
     * Base constructor. Sets up only the card's initial cost and character
     */
    Wizard() {
        super(CharacterType.WIZARD);
    }

    /**
     * Returns a copy of the passed Character
     *
     * @param old Character to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private Wizard(Character old) {
        super(old);
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    Character shallowCopy() {
        return new Wizard(this);
    }

    /**
     * Pick one student color and exclude it from influence calculation. This card uses 1 step. The parameters are the
     * following:
     *
     * <ul>
     *     <li>color: color of the student to ignore</li>
     * </ul>
     *
     * @param phase the {@link ActionPhase} the card's effect has been called from
     * @param steps an array of {@link CharacterStep} configuring the effect
     * @return a Tuple containing the updated ActionPhase and the updated Character
     * @throws IllegalArgumentException           if {@code phase} or {@code steps} or are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code steps} is formatted incorrectly
     */
    @Override
    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep[] steps) throws InvalidCharacterParameterException {
        checkEffectParameters(phase, steps, 1);
        PieceColor toIgnore = steps[0].getParameterAsColor("color");
        return super.doEffect(phase, steps)
                .map(t -> {
                    ActionPhase p = t.getFirst();
                    InfluenceCalculator i = p.getInfluenceCalculator();
                    return new Tuple<>(
                            p.setInfluenceCalculator(new RemoveStudentInfluenceDecorator(i, toIgnore)),
                            t.getSecond());
                });
    }
}
