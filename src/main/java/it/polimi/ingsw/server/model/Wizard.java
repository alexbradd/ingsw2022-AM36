package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

/**
 * Represents the Wizard card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Wizard extends Character {
    /**
     * Create a new Wizard card.
     */
    Wizard() {
        super(Characters.WIZARD, 3);
    }

    /**
     * Pick one student color and exclude it from influence calculation.
     *
     * <ul>
     *     <li>Position 0: Color of the student to ignore</li>
     *     <li>Position 1...: ignored</li>
     * </ul>
     *
     * @param phase      the {@link ActionPhase} the card's effect has been called from
     * @param parameters an array of strings configuring the effect
     * @throws IllegalArgumentException           if any of the parameters are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code parameters} is formatted
     *                                            incorrectly
     */
    @Override
    void doEffect(ActionPhase phase, String[] parameters) throws InvalidCharacterParameterException {
        super.doEffect(phase, parameters);
        if (parameters == null) throw new IllegalArgumentException("parameters shouldn't be null");
        if (parameters.length == 1) throw new
                InvalidCharacterParameterException("wrong array size: should be 1");

        PieceColor c = parseColor(parameters, 0);
        phase.setInfluenceCalculator(new RemoveStudentInfluenceDecorator(phase.getInfluenceCalculator(), c));
    }
}
