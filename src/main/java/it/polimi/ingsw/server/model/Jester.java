package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.enums.CharacterType;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the Jester card.
 *
 * @author Alexandru Gabriel Bradatan
 */
public class Jester extends StudentStoreCharacter {
    /**
     * Base constructor. Sets up only the card's initial cost and character
     */
    Jester() {
        super(CharacterType.JESTER, 6);
    }

    /**
     * Returns a copy of the passed Character
     *
     * @param old Character to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private Jester(Jester old) {
        super(old);
    }

    /**
     * Swaps up to 3 students of choice from this card with as many from the current player's Entrance. This card uses
     * up to 3 steps, any more passed will be ignored. The parameters used in each step are the following:
     *
     * <ul>
     *     <li>card: the color of the student to move from this card to the Entrance</li>
     *     <li>entrance: the color of the student to move from the Entrance to this card</li>
     * </ul>
     *
     * @param phase the {@link ActionPhase} the card's effect has been called from
     * @param steps an array of {@link CharacterStep} configuring the effect
     * @return a Tuple containing the updated ActionPhase and the updated Character
     * @throws IllegalArgumentException           if {@code phase} or {@code steps} or are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code steps} is formatted incorrectly
     * @throws InvalidPhaseUpdateException        if the effect of the card would modify the state in an illegal way
     */
    @Override
    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep... steps) throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        checkEffectParameters(phase, steps);
        List<Tuple<PieceColor, PieceColor>> colors = fromStepToTuple(steps);
        return super.doEffect(phase, steps)
                .throwMap(t -> {
                    Player p = t.getFirst().getCurrentPlayer();
                    for (Tuple<PieceColor, PieceColor> color : colors) {
                        t = t.throwMap((actionPhase, character) -> {
                            StudentStoreCharacter studentStore = (StudentStoreCharacter) character;
                            return studentStore.moveFromHere(
                                    new Tuple<>(actionPhase, studentStore),
                                    color.getFirst(),
                                    (ap, student) -> ap.addToEntrance(p, student),
                                    (ap, ssc) -> ap
                                            .getFromEntrance(p, color.getSecond())
                                            .map((newAp, s) -> new Tuple<>(newAp, ssc.add(s))));

                        });
                    }
                    return t;
                });
    }

    /**
     * Converts a {@link CharacterStep} into a Tuple containing:
     *
     * <ol>
     *     <li>as first, the color to take from the card</li>
     *     <li>as second, the color to take from the player's entrance</li>
     * </ol>
     *
     * @param steps the array of steps to convert
     * @return a List of Tuple
     * @throws InvalidCharacterParameterException if the {@link CharacterStep} is not formatted correctly
     */
    private List<Tuple<PieceColor, PieceColor>> fromStepToTuple(CharacterStep[] steps) throws InvalidCharacterParameterException {
        List<Tuple<PieceColor, PieceColor>> colors = new ArrayList<>(2);
        for (int i = 0; i < getCharacterType().getMaxSteps() && i < steps.length; i++) {
            PieceColor card = steps[i].getParameterAsColor("card");
            PieceColor entrance = steps[i].getParameterAsColor("entrance");
            colors.add(new Tuple<>(card, entrance));
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
        return new Jester(this);
    }
}
