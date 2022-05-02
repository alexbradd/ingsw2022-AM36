package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.EmptyContainerException;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 * Represents the Thief card
 *
 * @author Alexandru Gabriel Bradatan
 */
class Thief extends Character {
    /**
     * Base constructor. Sets up only the card's initial cost and character
     */
    Thief() {
        super(CharacterType.THIEF);
    }

    /**
     * Returns a copy of the passed Character
     *
     * @param old Character to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private Thief(Thief old) {
        super(old);
    }

    /**
     * Every Player must put at two students of the chosen color from their hall in the Sack. If they don't have enough,
     * they will simply put every one they have. This card uses 1 step. The parameters used are the following:
     *
     * <ul>
     *     <li>"color": the color of the students to move</li>
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
        PieceColor color = steps[0].getParameterAsColor("color");
        return super.doEffect(phase, steps)
                .map((actionPhase, character) -> {
                    ArrayList<Student> removed = new ArrayList<>();
                    actionPhase = actionPhase.forEachPlayer(b -> b.updateHall(h -> {
                        for (int i = 0; i < 2; i++) {
                            try {
                                h = h.remove(color).map((nh, s) -> {
                                    removed.add(s);
                                    return nh;
                                });
                            } catch (EmptyContainerException | EmptyStackException ignored) {
                                break;
                            }
                        }
                        return h;
                    })).putInSack(removed);
                    return new Tuple<>(actionPhase, character);
                });
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    Character shallowCopy() {
        return new Thief(this);
    }
}
