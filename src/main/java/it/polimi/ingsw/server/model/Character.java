package it.polimi.ingsw.server.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

import java.util.Objects;

/**
 * Represents an abstract character card. Each card has one "character", a cost that goes up by 1 after the first
 * use and two hooks into respectively the {@link PreparePhase} and {@link ActionPhase} of the game. This base class has
 * only default implementations that either do nothing, or throw an exception. Please see all subclasses for different
 * card effects.
 *
 * @author Alexandru Gabriel Bradatan
 * @see ActionPhase
 * @see PreparePhase
 */
abstract class Character implements Jsonable {
    /**
     * This card's character. Represented as a {@link CharacterType} enum.
     */
    private final CharacterType characterType;

    /**
     * The base cost of the card.
     */
    private final int initialCost;

    /**
     * Indicates if a card has been used at least once.
     */
    private boolean used;

    /**
     * Base constructor. Sets up only the card's initial cost and character
     *
     * @param characterType this card's character
     * @throws IllegalArgumentException if {@code characterType} is null
     */
    Character(CharacterType characterType) {
        if (characterType == null) throw new IllegalArgumentException("characterType shouldn't be null");
        this.characterType = characterType;
        this.initialCost = characterType.getInitialCost();
        used = false;
    }

    /**
     * Returns a copy of the passed Character
     *
     * @param old Character to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    Character(Character old) {
        if (old == null) throw new IllegalArgumentException("old shouldn't be null");
        this.characterType = old.characterType;
        this.initialCost = this.characterType.getInitialCost();
        this.used = old.used;
    }

    /**
     * Abstract method that returns a shallow copy of the current object.
     *
     * @return returns a shallow copy of the current object.
     */
    abstract Character shallowCopy();

    /**
     * Getter for this card's CharacterType
     *
     * @return this card's CharacterType
     */
    CharacterType getCharacterType() {
        return characterType;
    }

    /**
     * Getter for this card's actual cost. This means that this will return the card's initial cost if this card hasn't
     * been used before and the initial cos +1 otherwise.
     *
     * @return this card's actual cost
     */
    int getCost() {
        return used ? initialCost + 1 : initialCost;
    }

    /**
     * Default implementation for the {@link PreparePhase} hook. This implementation only checks for nullity, so if
     * subclasses want to not rewrite it themselves, they can call this implementation.
     *
     * @param phase the {@link PreparePhase} the card's hook has been called from
     * @return a Tuple containing the updated PreparePhase and the updated Character
     * @throws IllegalArgumentException if {@code phase} is null
     */
    Tuple<PreparePhase, Character> doPrepare(PreparePhase phase) {
        if (phase == null) throw new IllegalArgumentException("phase shouldn't be null");
        return new Tuple<>(phase, this);
    }

    /**
     * Default implementation for the card's effect. This implementation only marks the card as used and checks for
     * nullity of parameters. All subclasses should call this implementation if they are overriding this method.
     * <p>
     * The number of steps and the parameters of each are detailed in each subclass. To signal that the player has not
     * provided any steps, an empty array should be passed.
     *
     * @param phase the {@link ActionPhase} the card's effect has been called from
     * @param steps an array of {@link CharacterStep} configuring the effect
     * @return a Tuple containing the updated ActionPhase and the updated Character
     * @throws IllegalArgumentException           if {@code phase} or {@code steps} or are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code steps} is formatted incorrectly
     * @throws InvalidPhaseUpdateException        if the effect of the card would modify the state in an illegal way
     */
    Tuple<ActionPhase, Character> doEffect(ActionPhase phase, CharacterStep... steps) throws InvalidCharacterParameterException, InvalidPhaseUpdateException {
        if (phase == null) throw new IllegalArgumentException("phase shouldn't be null");
        if (steps == null) throw new IllegalArgumentException("steps shouldn't be null");
        Character t = this.shallowCopy();
        t.used = true;
        return new Tuple<>(phase, t);
    }

    /**
     * Helper for validating parameters in subclasses: check for nullity and length of the step array
     *
     * @param phase        the ActionPhase to check
     * @param steps        the CharacterStep array to check
     * @param minimumSteps the minimum number of steps that {@code steps} should have
     * @throws IllegalArgumentException           in case of nullity
     * @throws InvalidCharacterParameterException if the length of {@code steps} is less than {@code minimumSteps}
     */
    void checkEffectParameters(ActionPhase phase, CharacterStep[] steps, int minimumSteps) throws InvalidCharacterParameterException {
        if (phase == null) throw new IllegalArgumentException("phase shouldn't be null");
        if (steps == null) throw new IllegalArgumentException("steps shouldn't be null");
        if (steps.length < minimumSteps)
            throw new InvalidCharacterParameterException("Too few parameters: at least " + minimumSteps + " expected");
        for (CharacterStep s : steps)
            if (s == null) throw new IllegalArgumentException("Step shouldn't be null");
    }

    /**
     * Returns the number of {@link BlockCard} available on this card. This implementation always returns 0 since
     * block related methods always throw an exception.
     *
     * @return the number of {@link BlockCard} available on this card
     */
    int getNumOfBlocks() {
        return 0;
    }

    /**
     * Adds the given {@link BlockCard} to the internal store. This implementation of the method throws a
     * {@link UnsupportedOperationException}.
     *
     * @param block the {@link BlockCard} to add to the store
     * @return the updated Character
     * @throws UnsupportedOperationException always
     */
    Character pushBlock(BlockCard block) {
        throw new UnsupportedOperationException("this card doesn't receive blocks");
    }

    /**
     * Pops a {@link BlockCard} from the internal store and returns it. This implementation of the method throws a
     * {@link UnsupportedOperationException}.
     *
     * @return a Tuple containing the new Character and a BlockCard.
     * @throws UnsupportedOperationException always
     */
    Tuple<Character, BlockCard> popBlock() {
        throw new UnsupportedOperationException("this card doesn't produce blocks");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character character = (Character) o;
        return initialCost == character.initialCost && used == character.used && characterType == character.characterType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(characterType, initialCost, used);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JsonElement toJson() {
        JsonObject ret = new JsonObject();

        ret.addProperty("type", characterType.toString());
        ret.addProperty("cost", getCost());
        return ret;
    }
}