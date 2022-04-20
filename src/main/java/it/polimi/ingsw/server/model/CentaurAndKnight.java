package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;

import java.util.Objects;

/**
 * Represents the Centaur character card.
 *
 * @author Alexandru Gabriel Bradatan
 */
class CentaurAndKnight extends Character {
    private final Behaviour behaviour;

    /**
     * Creates a new CentaurAndKnight card. If {@code behaviour} is {@link Behaviour#CENTAUR} the card will
     * act as a Centaur card. Otherwise, it will act a Knight card.
     *
     * @param behaviour an enum describing what this card will act as
     */
    CentaurAndKnight(Behaviour behaviour) {
        super(behaviour == Behaviour.CENTAUR ? CharacterType.CENTAUR : CharacterType.KNIGHT);
        this.behaviour = behaviour;
    }

    /**
     * Returns a shallow copy of the given card
     *
     * @param old the Centaur to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private CentaurAndKnight(CentaurAndKnight old) {
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
        return new CentaurAndKnight(this);
    }

    /**
     * If acting as Centaur: Ignore the additional influence of towers during influence calculation. This effect requires no steps.
     * If acting as Knight: Ignore the additional influence of towers during influence calculation. This effect requires no steps.
     *
     * @param phase the {@link ActionPhase} the card's effect has been called from
     * @param steps an array of {@link CharacterStep} configuring the effect
     * @return a Tuple containing the updated ActionPhase and the updated Character
     * @throws IllegalArgumentException           if {@code phase} or {@code steps} or are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code steps} is formatted incorrectly
     */
    @Override
    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep[] steps) throws InvalidCharacterParameterException {
        return super.doEffect(phase, steps)
                .map(t -> {
                    ActionPhase newPhase = t.getFirst();
                    Player p = newPhase.getCurrentPlayer();
                    CentaurAndKnight c = (CentaurAndKnight) t.getSecond();
                    if (c.behaviour == Behaviour.CENTAUR) {
                        newPhase = newPhase.setInfluenceCalculator(
                                new IgnoreTowersInfluenceDecorator(newPhase.getInfluenceCalculator()));
                    } else {
                        newPhase = newPhase.setInfluenceCalculator(
                                new ExtraPointsInfluenceDecorator(newPhase.getInfluenceCalculator(), p, 2));
                    }
                    return new Tuple<>(newPhase, c);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CentaurAndKnight that = (CentaurAndKnight) o;
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
        CENTAUR, KNIGHT
    }
}