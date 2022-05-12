package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

import java.util.Objects;

/**
 * Represents the Centaur, Knight and Wizard character cards.
 *
 * @author Alexandru Gabriel Bradatan
 */
class InfluenceDecoratingCharacter extends Character {
    private final Behaviour behaviour;

    /**
     * Creates a new InfluenceDecoratingCharacter card. The {@link Behaviour} enum specifies which card this class
     * will act as. The default value is WIZARD.
     *
     * @param behaviour an enum describing what this card will act as
     */
    InfluenceDecoratingCharacter(Behaviour behaviour) {
        super(switch (behaviour) {
            case CENTAUR -> CharacterType.CENTAUR;
            case KNIGHT -> CharacterType.KNIGHT;
            default -> CharacterType.WIZARD;
        });
        this.behaviour = behaviour;
    }

    /**
     * Returns a shallow copy of the given card
     *
     * @param old the Centaur to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private InfluenceDecoratingCharacter(InfluenceDecoratingCharacter old) {
        super(old);
        this.behaviour = old.behaviour;
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    @Override
    Character shallowCopy() {
        return new InfluenceDecoratingCharacter(this);
    }

    /**
     * If acting as Centaur: Ignore the additional influence of towers during influence calculation. This effect requires no steps.
     * If acting as Knight: Ignore the additional influence of towers during influence calculation. This effect requires no steps.
     * If acting as Wizard: Pick one student color and exclude it from influence calculation. This card uses 1 step. The
     * parameters are the following:
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
    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep... steps) throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        checkEffectParameters(phase, steps, 0);
        var colorWrapper = new Object() {
            PieceColor color = null;
        };
        if (behaviour == Behaviour.WIZARD) {
            if (steps.length < 1)
                throw new InvalidCharacterParameterException("Too few parameters: at least 1 expected");
            colorWrapper.color = steps[0].getParameterAsColor("color");
        }
        return super.doEffect(phase, steps)
                .map((newPhase, character) -> {
                    Player p = newPhase.getCurrentPlayer();
                    InfluenceDecoratingCharacter c = (InfluenceDecoratingCharacter) character;
                    newPhase = switch (c.behaviour) {
                        case CENTAUR -> doCentaur(newPhase);
                        case KNIGHT -> doKnight(newPhase, p);
                        default -> doWizard(newPhase, colorWrapper.color);
                    };
                    return new Tuple<>(newPhase, c);
                });
    }

    /**
     * Executes the Centaur behaviour
     *
     * @param p the ActionPhase to modify
     * @return a new ActionPhase with the changes applied
     */
    private ActionPhase doCentaur(ActionPhase p) {
        return p.setInfluenceCalculator(new IgnoreTowersInfluenceDecorator(p.getInfluenceCalculator()));
    }

    /**
     * Executes the Knight behaviour
     *
     * @param ph the ActionPhase to modify
     * @param p  the player to pass to the InfluenceDecorator
     * @return a new ActionPhase with the changes applied
     */
    private ActionPhase doKnight(ActionPhase ph, Player p) {
        return ph.setInfluenceCalculator(
                new ExtraPointsInfluenceDecorator(ph.getInfluenceCalculator(), p, 2));
    }

    /**
     * Executes the Wizard behaviour
     *
     * @param p the ActionPhase to modify
     * @param c the color to pass to the InfluenceDecorator
     * @return a new ActionPhase with the changes applied
     */
    private ActionPhase doWizard(ActionPhase p, PieceColor c) {
        return p.setInfluenceCalculator(new RemoveStudentInfluenceDecorator(p.getInfluenceCalculator(), c));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        InfluenceDecoratingCharacter that = (InfluenceDecoratingCharacter) o;
        return behaviour == that.behaviour;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), behaviour);
    }

    /**
     * Enum for signaling what this card will act as
     */
    public enum Behaviour {
        CENTAUR, KNIGHT, WIZARD
    }
}