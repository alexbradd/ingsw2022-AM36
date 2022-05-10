package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the Jester card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Jester extends StudentStoreCharacter {
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
     */
    @Override
    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep[] steps) throws InvalidCharacterParameterException {
        checkEffectParameters(phase, steps, 0);
        List<Tuple<PieceColor, PieceColor>> colors = fromStepToTuple(steps);
        verifyEntranceSize(phase, colors);
        verifyCardSize(colors);
        return super.doEffect(phase, steps)
                .map((actionPhase, character) -> {
                    Player p = actionPhase.getCurrentPlayer();
                    StudentStoreCharacter studentStore = (StudentStoreCharacter) character;
                    Tuple<ActionPhase, Character> t = new Tuple<>(actionPhase, character);
                    for (Tuple<PieceColor, PieceColor> color : colors) {
                        t = actionPhase
                                .unsafeGetFromEntrance(p, color.getSecond())
                                .map((ap, studentEntrance) -> studentStore
                                        .remove(color.getFirst())
                                        .map((ssc, studentCard) -> new Tuple<>(ssc.add(studentEntrance), studentCard))
                                        .map((ssc, studentCard) -> new Tuple<>(
                                                ap.updateEntrance(p, e -> e.add(studentCard)),
                                                ssc)));
                    }
                    return t;
                });
    }

    private List<Tuple<PieceColor, PieceColor>> fromStepToTuple(CharacterStep[] steps) throws InvalidCharacterParameterException {
        List<Tuple<PieceColor, PieceColor>> colors = new ArrayList<>(2);
        for (int i = 0; i < 3 && i < steps.length; i++) {
            PieceColor card = steps[i].getParameterAsColor("card");
            PieceColor entrance = steps[i].getParameterAsColor("entrance");
            colors.add(new Tuple<>(card, entrance));
        }
        return colors;
    }

    void verifyEntranceSize(ActionPhase phase, List<Tuple<PieceColor, PieceColor>> colors) throws InvalidCharacterParameterException {
        Player current = phase.getCurrentPlayer();
        if (phase.getTable().getBoardOf(current).getEntrance().size() < colors.size())
            throw new InvalidCharacterParameterException("Wrong invocation: not enough students in entrance");
        for (Tuple<PieceColor, PieceColor> tuple : colors) {
            PieceColor entranceColor = tuple.getSecond();
            long n = colors.stream().filter(t -> t.getSecond().equals(entranceColor)).count();
            if (phase.getTable().getBoardOf(current).getEntrance().size(entranceColor) < n)
                throw new InvalidCharacterParameterException("Wrong invocation: not enough students of color" + entranceColor + " in entrance");
        }
    }

    void verifyCardSize(List<Tuple<PieceColor, PieceColor>> colors) throws InvalidCharacterParameterException {
        HashMap<PieceColor, Integer> map = new HashMap<>();
        for (Tuple<PieceColor, PieceColor> p : colors)
            map.merge(p.getFirst(), 1, Integer::sum);
        for (Map.Entry<PieceColor, Integer> e : map.entrySet()) {
            if (size(e.getKey()) < e.getValue())
                throw new InvalidCharacterParameterException("Wrong invocation: not enough students of color" + e.getKey() + " on card");
        }
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
