package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.Characters;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

/**
 * Represents the Bard character card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Bard extends Character {
    /**
     * Creates a new Bard card.
     */
    Bard() {
        super(Characters.BARD, 1);
    }

    /**
     * Swaps 2 students of choice from the current player's Entrance with 2 of the same player's Hall. Parameter layout:
     *
     * <ul>
     *     <li>Position 0: number of swaps</li>
     *     <li>Position 1-2: Color of student from Entrance and color of student from Hall</li>
     *     <li>Position 3-4: Color of student from Entrance and color of student from Hall</li>
     *     <li>Position 5...: ignored</li>
     * </ul>
     *
     * @param phase      the {@link ActionPhase} the card's effect has been called from
     * @param parameters an array of strings configuring the effect
     * @throws IllegalArgumentException           if any parameter is null
     * @throws InvalidCharacterParameterException if any of the strings in {@code parameters} is formatted
     *                                            incorrectly
     */
    @Override
    void doEffect(ActionPhase phase, String[] parameters) throws InvalidCharacterParameterException {
        super.doEffect(phase, parameters);
        if (parameters == null) throw new IllegalArgumentException("parameters shouldn't be null");
        if (parameters.length < 1) throw new
                InvalidCharacterParameterException("wrong array size: should be at least 1");

        int nSwaps = parseInteger(parameters, 0);
        boundCheckSwaps(nSwaps);
        sizeCheckParameters(parameters.length, nSwaps);
        for (int i = 0; i < nSwaps; i++) {
            PieceColor c1 = parseColor(parameters, 1 + i * 2),
                    c2 = parseColor(parameters, 2 + i * 2);
            phase.swapStudents(c1, c2, phase.getPlayerEntrance(), phase.getPlayerHall());
        }
    }

    /**
     * Checks if number of swaps are within bounds ([0;2]).
     *
     * @param nSwaps number of swaps
     * @throws InvalidCharacterParameterException if number of swaps are out of bounds
     */
    private void boundCheckSwaps(int nSwaps) throws InvalidCharacterParameterException {
        if (nSwaps < 0 || nSwaps > 2) {
            String msg = InvalidCharacterParameterException.message(0, "should be between 0 and 2");
            throw new InvalidCharacterParameterException(msg);
        }
    }

    /**
     * Checks if parameter array length is coherent with declare number of swaps.
     *
     * @param parameterLength parameter array length
     * @param nSwaps          number of swaps
     * @throws InvalidCharacterParameterException if parameter array length is not coherent with declare number of swaps
     */
    private void sizeCheckParameters(int parameterLength, int nSwaps) throws InvalidCharacterParameterException {
        if (parameterLength != nSwaps * 2 + 1)
            throw new InvalidCharacterParameterException("wrong array size: too short for declared number of steps");
    }
}
