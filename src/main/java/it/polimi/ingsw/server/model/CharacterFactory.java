package it.polimi.ingsw.server.model;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Represents a factory for generating {@link Character} cards.
 *
 * @author Alexadnru Gabriel Bradatan
 * @see Character
 */
public class CharacterFactory {
    /**
     * ArrayList containing {@link Supplier} function of all {@link Character} cards, populated during static
     * initialization. This is a bit ugly, but I really don't want to bring reflection into this.
     */
    private static final ArrayList<Supplier<Character>> allCards;

    /**
     * Private empty constructor, since this class is completely static and should not be instantiated.
     */
    private CharacterFactory() {
    }

    static {
        allCards = new ArrayList<>(12);
        allCards.add(Priest::new);
        allCards.add(Innkeeper::new);
        allCards.add(Herald::new);
        allCards.add(Messenger::new);
        allCards.add(Herbalist::new);
        allCards.add(Centaur::new);
        allCards.add(Jester::new);
        allCards.add(Knight::new);
        allCards.add(Wizard::new);
        allCards.add(Bard::new);
        allCards.add(Princess::new);
        allCards.add(Thief::new);
    }

    /**
     * Returns an array of three randomly chosen {@link Character} cards.
     *
     * @return an array of three randomly chosen {@link Character}
     */
    public static Character[] pickThreeRandom() {
        ArrayList<Character> toRet = new ArrayList<>(3);
        ArrayList<Integer> chosenIndexes = new ArrayList<>(3);

        Random r = new Random();
        int chosen = 0;
        while (chosen < 3) {
            int newCard = r.nextInt(allCards.size());
            if (!chosenIndexes.contains(newCard)) {
                toRet.add(createCharacterWithId(newCard));
                chosenIndexes.add(newCard);
                chosen++;
            }
        }
        return toRet.toArray(new Character[3]);
    }

    /**
     * Creates the {@link Character} card with the given id.
     *
     * @param id id of the {@link Character} to create
     * @return a new instance of the {@link Character} with the given id
     */
    private static Character createCharacterWithId(int id) {
        return allCards.get(id).get();
    }
}
