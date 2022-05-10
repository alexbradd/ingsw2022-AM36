package it.polimi.ingsw.server.model;

import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.model.enums.CharacterType;
import it.polimi.ingsw.server.model.enums.PieceColor;
import it.polimi.ingsw.server.model.exceptions.InvalidCharacterParameterException;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

import java.util.Objects;
import java.util.function.BiFunction;

class PriestAndPrincess extends StudentStoreCharacter {
    private final Behaviour behaviour;

    /**
     * Creates a new PriestAndPrincess card. If {@code behaviour} is {@link Behaviour#PRIEST} the card will
     * act as a Priest card. Otherwise, it will act a Princess card.
     *
     * @param behaviour an enum describing what this card will act as
     */
    PriestAndPrincess(Behaviour behaviour) {
        super(behaviour == Behaviour.PRIEST ? CharacterType.PRIEST : CharacterType.PRINCESS, 4);
        this.behaviour = behaviour;
    }

    /**
     * Returns a copy of the passed Character
     *
     * @param old Character to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    private PriestAndPrincess(PriestAndPrincess old) {
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
        return new PriestAndPrincess(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Tuple<PreparePhase, Character> doPrepare(PreparePhase phase) {
        return super.doPrepare(phase);
    }

    /**
     * If acting as Priest: Pick one student from the card and place it on the Island at given index. The card uses 1
     * step. Parameters are the following:
     *
     * <ul>
     *     <li>card: color of the student to move from this card to the Island</li>
     *     <li>island: island index on which to place the Student</li>
     * </ul>
     * <p>
     * If acting as Princess: Pick one student from the card and place it on the current player's Hall. The card uses 1
     * step. Parameters are the following:
     *
     * <ul>
     *     <li>card: color of the student to move from this card to the Island</li>
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
        if (behaviour == Behaviour.PRIEST)
            return doPriest(super.doEffect(phase, steps), steps);
        else
            return doPrincess(super.doEffect(phase, steps), steps);
    }

    /**
     * Executes Priest's effect
     *
     * @param tuple a tuple with the starting ActionPhase and Character
     * @param steps an array of {@link CharacterStep} configuring the effect
     * @return a Tuple containing the updated ActionPhase and the updated Character
     * @throws IllegalArgumentException           if {@code phase} or {@code steps} or are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code steps} is formatted incorrectly
     */
    private Tuple<ActionPhase, Character> doPriest(Tuple<ActionPhase, Character> tuple, CharacterStep[] steps) throws InvalidCharacterParameterException {
        PieceColor color = steps[0].getParameterAsColor("card");
        int island = steps[0].getParameterAsIslandIndex("island", tuple.getFirst());
        PriestAndPrincess card = (PriestAndPrincess) tuple.getSecond();
        if (card.size(color) < 1)
            throw new InvalidCharacterParameterException("Wrong invocation: card hasn't got enough students");
        return moveFromHere(new Tuple<>(tuple.getFirst(), card), color,
                (ap, student) -> {
                    try {
                        return ap.updateIsland(ap.getCurrentPlayer(), island, i -> i.add(student));
                    } catch (InvalidPhaseUpdateException e) {
                        e.printStackTrace();
                    }
                    return ap;
                },
                this::fromSack);
    }

    /**
     * Executes Princess's effect
     *
     * @param tuple a tuple with the starting ActionPhase and Character
     * @param steps an array of {@link CharacterStep} configuring the effect
     * @return a Tuple containing the updated ActionPhase and the updated Character
     * @throws IllegalArgumentException           if {@code phase} or {@code steps} or are null
     * @throws InvalidCharacterParameterException if any of the parameters in {@code steps} is formatted incorrectly
     */
    private Tuple<ActionPhase, Character> doPrincess(Tuple<ActionPhase, Character> tuple, CharacterStep[] steps) throws InvalidCharacterParameterException {
        ActionPhase actionPhase = tuple.getFirst();
        Player currentPlayer = actionPhase.getCurrentPlayer();
        PieceColor color = steps[0].getParameterAsColor("card");
        PriestAndPrincess card = (PriestAndPrincess) tuple.getSecond();
        if (card.size(color) < 1)
            throw new InvalidCharacterParameterException("Wrong invocation: card hasn't got enough students");
        if (actionPhase.getTable().getBoardOf(currentPlayer).getHall().isFull(color))
            throw new InvalidCharacterParameterException("Wrong invocation: current player's hall is full");

        return moveFromHere(new Tuple<>(tuple.getFirst(), card), color,
                (ap, student) -> ap.updateHall(currentPlayer, h -> h.add(student)),
                this::fromSack);
    }

    /**
     * To be fed to {@link StudentStoreCharacter#moveFromHere(Tuple, PieceColor, BiFunction, BiFunction)}, puts a
     * character from the sack onto the card
     *
     * @param ap        the ActionPhase
     * @param character the StudentStoreCharacter where the card will be put
     * @return a Tuple containing the updated ActionPhase and the updated Character
     */
    private Tuple<ActionPhase, StudentStoreCharacter> fromSack(ActionPhase ap, StudentStoreCharacter character) {
        return ap.drawStudent()
                .map((newAp, student) -> student
                        .map(s -> new Tuple<>(newAp, character.add(s)))
                        .orElse(new Tuple<>(newAp, character)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PriestAndPrincess that = (PriestAndPrincess) o;
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
        PRIEST, PRINCESS
    }
}
