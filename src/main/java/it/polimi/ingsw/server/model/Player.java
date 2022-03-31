package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.TowerColor;

import java.util.*;

/**
 * Represents the game's {@code Player} entity.
 * The class contains (among other things):
 * a deck of {@code Assistants}, a set of {@code Towers}, the player's {@code Hall} and the player's {@code Entrance}.
 *
 * @author Mattia Busso
 * @see Assistant
 * @see Entrance
 * @see Hall
 * @see Tower
 */
class Player {

    /**
     * The username of the player.
     */
    private final String username;

    /**
     * The deck of {@link Assistant} of the player.
     */
    private final List<Assistant> assistants;

    /**
     * The {@link Assistant} last-played by the player.
     */
    private Assistant lastPlayed;

    /**
     * Player's {@link Entrance}.
     */
    private final Entrance entrance;

    /**
     * Player's {@link Hall}.
     */
    private final Hall hall;

    /**
     * Player's stack of {@link Tower}.
     */
    private final Stack<Tower> towers;

    /**
     * The maximum number of towers allowed.
     */
    private final int maxNumTowers;

    /**
     * The towers color.
     *
     * @see TowerColor
     */
    private final TowerColor towersColor;

    /**
     * Number of player's coins.
     */
    private int coins;

    /**
     * Flag that indicates if a deck of assistants has already been received.
     */
    private boolean deckAdded = false;

    /**
     * Player constructor.
     * Initializes all parameters and fills out the {@code towers}.
     *
     * @param name player's username
     * @param entranceSize size of the entrance
     * @param color the color of the towers
     * @throws IllegalArgumentException if {@code numTowers <= 0 || entranceSize <= 0}
     */
    Player(String name, int entranceSize, int numTowers, TowerColor color) throws IllegalArgumentException {
        if(numTowers <= 0) {
            throw new IllegalArgumentException("numTowers shouldn't  be <= 0");
        }
        username = name;
        assistants = new ArrayList<>();
        entrance = new Entrance(this, entranceSize);
        hall = new Hall(this);
        towers = new Stack<>();
        maxNumTowers = numTowers;
        towersColor = color;
        for(int i = 0; i < numTowers; i++) {
            towers.add(new Tower(color, this));
        }
    }

    // Assistant's deck management

    /**
     * {@code assistants} deck setter.
     *
     * @param assistants the given deck of assistants
     * @throws IllegalStateException if {@code deckAdded == true} (can't add a new deck if one is already present)
     * @throws IllegalArgumentException if {@code assistants == null}
     */
    void receiveDeck(List<Assistant> assistants) throws IllegalStateException, IllegalArgumentException {
        if(assistants == null) {
            throw new IllegalArgumentException("assistants should not be null");
        }
        if(deckAdded) {
            throw new IllegalStateException("can't add a new deck if one is already present");
        }
        this.assistants.addAll(assistants);
        deckAdded = true;
    }

    /**
     * Pops the given {@link Assistant} from the deck and plays it, placing it as {@code lastPlayedAssistant}.
     *
     * @param assistantIndex index of the given {@link Assistant} inside the deck
     * @throws IndexOutOfBoundsException if {@code assistantIndex} is outside {@code assistants} boundaries
     */
    void playAssistant(int assistantIndex) throws IndexOutOfBoundsException {
        try {
            lastPlayed = assistants.get(assistantIndex);
            assistants.remove(assistantIndex);
        }
        catch(IndexOutOfBoundsException e) {
            throw new IndexOutOfBoundsException("assistant_index should be inside assistants index boundaries");
        }
    }

    /**
     * {@code lastPlayedAssistant} getter.
     *
     * @return an {@code Optional of Assistant} (because initially {@code lastPlayedAssistant} could be {@code null})
     */
    Optional<Assistant> getLastPlayedAssistant() {
        return Optional.ofNullable(lastPlayed);
    }

    /**
     * Returns the deck of assistants.
     *
     * @return a copy of {@code assistants}
     */
    List<Assistant> getAssistants() {
        return new ArrayList<>(assistants);
    }

    // Towers management

    /**
     * The player sends a {@link Tower} away.
     *
     * @return the tower sent away
     * @throws IllegalStateException if {@code towers} is empty
     */
    Tower sendTower() throws IllegalStateException {
        try {
            return towers.pop();
        }
        catch(EmptyStackException e) {
            throw new IllegalStateException("can't send a tower if there are no towers left");
        }
    }

    /**
     * The player receives one {@link Tower}.
     *
     * @throws IllegalArgumentException if {@code tower == null}
     * @throws IllegalArgumentException if tower's color is incorrect
     * @throws IllegalStateException if {@code towers} is full
     */
    void receiveTower(Tower tower) throws IllegalArgumentException, IllegalStateException {
        if(tower == null) {
            throw new IllegalArgumentException("tower should not be null");
        }
        if(tower.getColor() != towersColor) {
            throw new IllegalArgumentException("tower's color should be correct");
        }
        if(getNumOfTowers() == maxNumTowers) {
            throw new IllegalStateException("can't add a new tower to full towers set");
        }
        towers.add(tower);
    }

    /**
     * Getter for the number of towers held by player.
     *
     * @return the number of towers held by the player
     */
    int getNumOfTowers() {
        return towers.size();
    }

    // Coins management

    /**
     * The player spends {@code num_coins} coins.
     *
     * @param numCoins the number of coins to be spent
     * @throws IllegalStateException if {@code coins - numCoins <= 0} (the player tries to spend coins it doesn't have)
     * @throws IllegalArgumentException if {@code numCoins <= 0}
     */
    void spendCoins(int numCoins) throws IllegalStateException, IllegalArgumentException {
        if(numCoins <= 0) {
            throw new IllegalArgumentException("num_coins can't be <= 0");
        }
        if(coins - numCoins < 0) {
            throw new IllegalStateException("can't spend coins that the player doesn't have");
        }
        coins -= numCoins;
    }

    /**
     * The player receives one coin.
     */
    void receiveCoin() {
        coins++;
    }

    // Basic getters and setters

    /**
     * Player's username getter.
     *
     * @return {@code username}
     */
    String getUsername() {
        return username;
    }

    /**
     * Player's {@link Entrance} getter.
     *
     * @return {@code entrance}
     */
    Entrance getEntrance() {
        return entrance;
    }

    /**
     * Player's {@link Hall} getter.
     *
     * @return {@code hall}
     */
    Hall getHall() {
        return hall;
    }

}
