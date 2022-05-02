package it.polimi.ingsw.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Represents a factory for generating {@link Character} cards.
 *
 * @author Alexandru Gabriel Bradatan
 * @see Character
 */
class CharacterFactory {
    /**
     * ArrayList containing {@link Supplier} function of all {@link Character} cards, populated during static
     * initialization. This is a bit ugly, but I really don't want to bring reflection into this.
     */
    static final List<Supplier<Character>> ALL_CARDS;
    /**
     * An ArrayList of all the Character cards eligible for extraction. By default, it is set to {@link #ALL_CARDS}.
     */
    private static List<Supplier<Character>> extractableCards;

    static {
        ALL_CARDS = List.of(
                () -> new PriestAndPrincess(PriestAndPrincess.Behaviour.PRIEST),
                Innkeeper::new,
                Herald::new,
                Messenger::new,
                Herbalist::new,
                () -> new InfluenceDecoratingCharacter(InfluenceDecoratingCharacter.Behaviour.CENTAUR),
                Jester::new,
                () -> new InfluenceDecoratingCharacter(InfluenceDecoratingCharacter.Behaviour.KNIGHT),
                () -> new InfluenceDecoratingCharacter(InfluenceDecoratingCharacter.Behaviour.WIZARD),
                Bard::new,
                () -> new PriestAndPrincess(PriestAndPrincess.Behaviour.PRINCESS),
                Thief::new);
        extractableCards = ALL_CARDS;
    }

    /**
     * Private empty constructor, since this class is completely static and should not be instantiated.
     */
    private CharacterFactory() {
    }

    /**
     * Returns an array of n randomly chosen {@link Character} cards.
     *
     * @param n the number of cars contained in the array
     * @return an array of three randomly chosen {@link Character}
     * @throws IllegalArgumentException if {@code n} is less than 1 or greater than the size of the list of extractable
     *                                  cards
     */
    public static Character[] pickNRandom(int n) {
        if (n <= 0) throw new IllegalArgumentException("n should be >= 1");
        if (n > extractableCards.size())
            throw new IllegalArgumentException("n shouldn't be greater than the amount of possible cards");
        ArrayList<Character> toRet = new ArrayList<>(n);
        ArrayList<Integer> chosenIndexes = new ArrayList<>(n);

        Random r = new Random();
        int chosen = 0;
        while (chosen < n) {
            int newCard = r.nextInt(extractableCards.size());
            if (!chosenIndexes.contains(newCard)) {
                toRet.add(createCharacterWithId(newCard));
                chosenIndexes.add(newCard);
                chosen++;
            }
        }
        return toRet.toArray(new Character[3]);
    }

    /**
     * Sets ths list cards extractable by {@link #pickNRandom(int)}.
     *
     * @param extractable a List of {@link Supplier} of Characters
     * @throws IllegalArgumentException if {@code extractable} is null or empty
     */
    static void setExtractableCards(List<Supplier<Character>> extractable) {
        if (extractable == null) throw new IllegalArgumentException("extractable shouldn't be null");
        if (extractable.isEmpty()) throw new IllegalArgumentException("extractable shouldn't be empty");
        extractableCards = new ArrayList<>(extractable);
    }

    /**
     * Creates the {@link Character} card with the given id.
     *
     * @param id id of the {@link Character} to create
     * @return a new instance of the {@link Character} with the given id
     */
    private static Character createCharacterWithId(int id) {
        return extractableCards.get(id).get();
    }
}
