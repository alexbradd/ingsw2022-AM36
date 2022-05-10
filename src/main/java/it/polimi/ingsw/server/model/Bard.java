package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Bard character card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Bard extends Character {
    /**
     * Base constructor. Sets up only the card's initial cost and character
     */
    Bard() {
        super(CharacterType.BARD);
    }

    /**
     * Returns a copy of the passed Character
     *
     * @param old Character to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private Bard(Character old) {
        super(old);
    }

    /**
     * Swaps 2 students of choice from the current player's Entrance with 2 of the same player's Hall. This card uses
     * up to 2 steps, any more passed will be ignored. The parameters used in each step are the following:
     *
     * <ul>
     *     <li>"entrance": the color of the student to move from the Entrance to the Hall</li>
     *     <li>"hall": the color of the student to move from the Hall to the Entrance</li>
     * </ul>
     *
     * @param phase the {@link ActionPhase} the card's effect has been called from
     * @param steps an array of {@link CharacterStep} configuring the effect
     * @return a Tuple containing the updated ActionPhase and the updated Character
     * @throws IllegalArgumentException           if {@code phase} or {@code steps} or are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code steps} is formatted incorrectly
     */
    @Override
    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep[] steps) throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        checkEffectParameters(phase, steps, 0);
        Player current = phase.getCurrentPlayer();
        List<Tuple<PieceColor, PieceColor>> colors = fromStepToTuple(steps);
        return super.doEffect(phase, steps)
                .throwMap(((actionPhase, character) -> {
                    for (Tuple<PieceColor, PieceColor> t : colors) {
                        actionPhase = actionPhase
                                .getFromEntrance(current, t.getFirst())
                                .throwMap((ap, studentEntrance) -> ap
                                        .getFromHall(current, t.getSecond())
                                        .map((ap1, studentHall) ->
                                                new Tuple<>(ap1, new Tuple<>(studentEntrance, studentHall))))
                                .throwMap((ap, tuple) -> ap
                                        .addToEntrance(current, tuple.getSecond())
                                        .addToHall(current, tuple.getFirst()));
                    }
                    return new Tuple<>(actionPhase, character);
                }));
    }

    /**
     * Converts a {@link CharacterStep} into a Tuple containing:
     *
     * <ol>
     *     <li>as first, the color to take from the player's entrance</li>
     *     <li>as second, the color to take from the player's hall</li>
     * </ol>
     *
     * @param steps the array of steps to convert
     * @return a List of Tuple
     * @throws InvalidCharacterParameterException if the {@link CharacterStep} is not formatted correctly
     */
    private List<Tuple<PieceColor, PieceColor>> fromStepToTuple(CharacterStep[] steps) throws InvalidCharacterParameterException {
        List<Tuple<PieceColor, PieceColor>> colors = new ArrayList<>(2);
        for (int i = 0; i < 2 && i < steps.length; i++) {
            PieceColor entrance = steps[i].getParameterAsColor("entrance");
            PieceColor hall = steps[i].getParameterAsColor("hall");
            colors.add(new Tuple<>(entrance, hall));
        }
        return colors;
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    Character shallowCopy() {
        return new Bard(this);
    }
}
