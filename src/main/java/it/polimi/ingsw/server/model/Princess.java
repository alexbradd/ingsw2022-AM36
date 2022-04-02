package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

/**
 * Represents the Princess card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Princess extends StudentStoreCharacter {
    /**
     * Creates a new Princess card.
     */
    Princess() {
        super(Characters.PRINCESS, 2, 4);
    }

    /**
     * Pick one student from the card and place it on the current player's Hall. Parameter layout:
     *
     * <ul>
     *     <li>Position 0: Color of the student to move</li>
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
        phase.moveStudent(c, this, phase.getPlayerHall());
        receiveStudent(phase.getSack().sendStudent());
    }
}
