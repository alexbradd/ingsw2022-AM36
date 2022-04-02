package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

/**
 * Represents the Herald character card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Herald extends Character {
    /**
     * Creates a new Herald card.
     */
    Herald() {
        super(Characters.HERALD, 3);
    }

    /**
     * Request an extra influence calculation on the given island. Parameter layout:
     *
     * <ul>
     *     <li>Position 0: island index on which the calculation should be performed</li>
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
        if (parameters.length < 1) throw new
                InvalidCharacterParameterException("wrong array size: should be at least 1");

        int islandIndex = parseInteger(parameters, 0);
        Island island = getIsland(phase, islandIndex);
        phase.requestExtraInfluenceCalculation(island);
    }
}
