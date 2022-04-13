package it.polimi.ingsw.server.model;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.enums.AssistantType;

/**
 * Represents the game's {@code Player} entity.
 * The class contains (among other things):
 * a deck of {@code Assistants}, a set of {@code Towers}, the player's {@code Hall} and the player's {@code Entrance}.
 *
 * @author Mattia Busso, Leonardo Bianconi
 * @see Assistant
 * @see BoundedStudentContainer
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
    private List<Assistant> assistants;

    /**
     * The {@link Assistant} last-played by the player.
     */
    private Assistant lastPlayed;

    /**
     * Player's {@code Entrance}.
     */
    private BoundedStudentContainer entrance;

    /**
     * Player's {@link Hall}.
     */
    private Hall hall;

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
    private boolean deckAdded;

    /**
     * Player constructor.
     * Initializes all parameters and fills out the {@code towers}.
     *
     * @param name         player's username
     * @param entranceSize size of the entrance
     * @param color        the color of the towers
     * @throws IllegalArgumentException if {@code numTowers <= 0 || entranceSize <= 0}
     */
    Player(String name, int entranceSize, int numTowers, TowerColor color) throws IllegalArgumentException {
        if (numTowers <= 0) {
            throw new IllegalArgumentException("numTowers shouldn't  be <= 0");
        }
        username = name;
        assistants = new ArrayList<>();
        lastPlayed = null;
        deckAdded = false;
        entrance = new BoundedStudentContainer(entranceSize);
        hall = new Hall();
        towers = new Stack<>();
        maxNumTowers = numTowers;
        towersColor = color;
        for (int i = 0; i < numTowers; i++) {
            towers.add(new Tower(color, this));
        }
    }


    /**
     * Constructor that creates a copy a Player instance passed.
     *
     * @param oldPlayer the old Player instance to copy
     * @throws IllegalArgumentException if the old Player is null
     */
    Player(Player oldPlayer) throws IllegalArgumentException {
        if (oldPlayer == null)
            throw new IllegalArgumentException("Old player must not be null.");

        username = oldPlayer.username;
        assistants = oldPlayer.assistants;
        lastPlayed = oldPlayer.lastPlayed;
        deckAdded = oldPlayer.deckAdded;
        entrance = oldPlayer.entrance;
        hall = oldPlayer.hall;
        towers = oldPlayer.towers;
        maxNumTowers = oldPlayer.maxNumTowers;
        towersColor = oldPlayer.towersColor;
        coins = oldPlayer.coins;
    }

    // Assistant's deck management

    /**
     * Pops the given {@link Assistant} from the deck and plays it, placing it as {@code lastPlayedAssistant}.
     * Returns a new Player instance, which hasn't got that assistant in his deck anymore.
     *
     * @param type the {@link AssistantType} of the assistant to play
     * @return the new Player instance
     * @throws IllegalArgumentException if the assistant passed is null
     * @throws NoSuchElementException   if there isn't such assistant in the player's deck
     */
    Player playAssistant(AssistantType type) throws IllegalArgumentException, NoSuchElementException {
        if (type == null) throw new IllegalArgumentException("Assistant type must not be null.");

        Player p = new Player(this);
        Assistant a = getAssistant(type);
        p.lastPlayed = a;
        p.assistants.remove(a);
        return p;
    }

    /**
     * Creates another Player instance with the specified deck of {@link Assistant}s, and it returns the new Player..
     *
     * @param assistantDeck the given deck of assistants
     * @return the new Player instance
     * @throws IllegalStateException    if {@code deckAdded == true} (can't add a new deck if one is already present)
     * @throws IllegalArgumentException if {@code assistants == null}
     */
    Player receiveDeck(List<Assistant> assistantDeck) throws IllegalArgumentException, IllegalStateException {
        if (assistantDeck == null)
            throw new IllegalArgumentException("Assistant deck must not be null.");
        if (deckAdded)
            throw new IllegalStateException("A deck for this player has already been added.");

        Player p = new Player(this);
        p.assistants = assistantDeck;
        p.deckAdded = true;
        return p;
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
     * The player sends a {@link Tower} away to a Consumer<{@link Tower}>, if he has at least one inside
     * {@link Player#towers}. This method then returns another Player instance with the updated internal state.
     *
     * @return the new Player instance
     * @throws IllegalStateException    if {@link Player#towers} is empty
     * @throws IllegalArgumentException if the Consumer passed is null
     */
    Player sendTower(Consumer<Tower> consumer) throws IllegalArgumentException, IllegalStateException {
        if (consumer == null) throw new IllegalArgumentException("Consumer must not be null.");
        if (towers.size() == 0) throw new IllegalStateException("The player has no towers.");

        Player p = new Player(this);
        consumer.accept(p.towers.pop());
        return p;
    }

    /**
     * The player receives one {@link Tower}. The method then returns another Player instance, containing that
     * tower.
     *
     * @throws IllegalArgumentException if {@code tower == null}
     * @throws IllegalArgumentException if tower's color is incorrect
     * @throws IllegalStateException    if {@code towers} is full
     */
    Player receiveTower(Tower t) throws IllegalArgumentException {
        if (t == null)
            throw new IllegalArgumentException("Tower must not be null.");
        if (!t.getColor().equals(towersColor))
            throw new IllegalArgumentException("The tower parameter has a different color from the player's towers.");
        if (towers.size() == maxNumTowers)
            throw new IllegalStateException("This player already has the maximum number of towers in his board.");

        Player p = new Player(this);
        p.towers.add(t);
        return p;
    }

    /**
     * Getter for the number of towers held by player.
     *
     * @return the number of towers held by the player
     */
    int getNumOfTowers() {
        return towers.size();
    }

    // Hall and entrance updates

    /**
     * This method applies an update to the player's hall, which is expressed via a
     * {@code Function<Hall, Hall>}, passed via parameter. This method then returns a copy
     * of the original Player instance, with the applied update.
     *
     * @param update the function to apply
     * @return the new Player instance
     * @throws IllegalArgumentException if the function passed is null
     */
    Player updateHall(Function<Hall, Hall> update) throws IllegalArgumentException {
        if (update == null)
            throw new IllegalArgumentException("Update must not be null.");

        Player p = new Player(this);
        p.hall = update.apply(p.hall);
        return p;
    }

    /**
     * This method applies an update to the player's entrance, which is expressed via a
     * {@code Function<BoundedContainer, BoundedContainer>}, passed via parameter. This method then returns a copy
     * of the original Player instance, with the applied update.
     *
     * @param update the function to apply
     * @return the new Player instance
     * @throws IllegalArgumentException if the function passed is null
     */
    Player updateEntrance(Function<BoundedStudentContainer, BoundedStudentContainer> update) throws IllegalArgumentException {
        if (update == null)
            throw new IllegalArgumentException("Update must not be null.");

        Player p = new Player(this);
        p.entrance = update.apply(entrance);
        return p;
    }

    // Coins management

    /**
     * The player spends {@code num_coins} coins. This method then returns a new Player instance with the updated
     * internal state
     *
     * @param numCoins the number of coins to be spent
     * @throws IllegalArgumentException if {@code numCoins <= 0}
     * @throws IllegalStateException    if {@code coins - numCoins <= 0} (the player tries to spend coins it doesn't have)
     */
    Player spendCoins(int numCoins) throws IllegalArgumentException, IllegalStateException {
        if (numCoins <= 0) throw new IllegalArgumentException("The number of coins spent cannot be less than 1");
        if (numCoins > coins) throw new IllegalStateException("Not enough coins.");

        Player p = new Player(this);
        p.coins -= numCoins;
        return p;
    }

    /**
     * The player receives one coin. A new Player instance with the updated internal state is then returned.
     *
     * @return the new Player instance
     */
    Player receiveCoin() throws IllegalArgumentException {
        Player p = new Player(this);
        p.coins++;
        return p;
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
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return username.equals(player.username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return username;
    }

    /**
     * Helper method that gets the assistant corresponding to a certain {@link AssistantType} passed via parameter,
     * if present in the player's deck. If not, it returns an exception.
     *
     * @param type the {@code AssistantType} of the assistant
     * @return the corresponding {@code Assistant} of the player's deck, if present
     * @throws NoSuchElementException if the player does not have the specified {@code Assistant}
     */
    private Assistant getAssistant(AssistantType type) throws NoSuchElementException {
        return assistants.stream()
                .filter(e -> e.getAssistantType().equals(type))
                .findFirst()
                .orElseThrow();
    }
}