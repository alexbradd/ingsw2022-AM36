package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

/**
 * Represents the Priest Card.
 */
class Priest extends StudentStoreCharacter {
    /**
     * Creates a new Priest card.
     */
    Priest() {
        super(Characters.PRIEST, 1, 4);
    }

    /**
     * Pick one student from the card and place it on the Island at given index. Parameter layout:
     *
     * <ul>
     *     <li>Position 0: Color of the student to move</li>
     *     <li>Position 1: island index on which to place the Student</li>
     *     <li>Position 2...: ignored</li>
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
        if (parameters.length == 2) throw new
                InvalidCharacterParameterException("wrong array size: should be 2");

        PieceColor c = parseColor(parameters, 0);
        int islandIndex = parseInteger(parameters, 1);
        Island i = getIsland(phase, islandIndex);

        phase.moveStudent(c, this, i);
        try {
            receiveStudent(phase.getSack().sendStudent());
        } catch (IllegalStateException ignore) {
        }
    }
}
