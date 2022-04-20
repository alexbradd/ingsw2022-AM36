package it.polimi.ingsw.server.model;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import it.polimi.ingsw.server.model.enums.TowerColor;
import it.polimi.ingsw.server.model.enums.AssistantType;

/**
 * Represents one player's game area. It includes the School board, the assistants deck and the coins the player has.
 * It also contains a reference to the player who owns the Board. All possible operations return a new Board instance,
 * representing the new state of the player's game area.
 *
 * @author Mattia Busso, Leonardo Bianconi
 * @see Assistant
 * @see BoundedStudentContainer
 * @see Hall
 * @see Tower
 * @see Player
 */

public class Board {
    /**
     * The {@link Player} who owns the board.
     */
    private final Player player;

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
     * Board constructor.
     * Initializes all parameters and fills out the {@code towers}.
     *
     * @param playerOwner  the player who owns the Board
     * @param entranceSize size of the entrance
     * @param numTowers    the initial number of towers of the player
     * @param color        the color of the towers
     * @throws IllegalArgumentException if {@code numTowers <= 0 || entranceSize <= 0}
     */
    Board(Player playerOwner, int entranceSize, int numTowers, TowerColor color) throws IllegalArgumentException {
        if (numTowers <= 0) {
            throw new IllegalArgumentException("numTowers shouldn't  be <= 0");
        }
        player = playerOwner;
        assistants = new ArrayList<>();
        lastPlayed = null;
        deckAdded = false;
        entrance = new BoundedStudentContainer(entranceSize);
        hall = new Hall();
        towers = new Stack<>();
        maxNumTowers = numTowers;
        towersColor = color;
        for (int i = 0; i < numTowers; i++) {
            towers.add(new Tower(color, this.getPlayer()));
        }
    }


    /**
     * Constructor that creates a copy of a Board instance passed.
     *
     * @param oldBoard the old Board instance to copy
     * @throws IllegalArgumentException if the old Board is null
     */
    Board(Board oldBoard) throws IllegalArgumentException {
        if (oldBoard == null)
            throw new IllegalArgumentException("Old player must not be null.");

        player = oldBoard.player;
        assistants = oldBoard.assistants;
        lastPlayed = oldBoard.lastPlayed;
        deckAdded = oldBoard.deckAdded;
        entrance = oldBoard.entrance;
        hall = oldBoard.hall;
        towers = oldBoard.towers;
        maxNumTowers = oldBoard.maxNumTowers;
        towersColor = oldBoard.towersColor;
        coins = oldBoard.coins;
    }


    // Assistant's deck management
    /**
     * Pops the given {@link Assistant} from the deck and plays it, placing it as {@code lastPlayedAssistant}.
     * Returns a new Board instance, which hasn't got that assistant in the deck anymore.
     *
     * @param type the {@link AssistantType} of the assistant to play
     * @return the new Board instance
     * @throws IllegalArgumentException if the assistant passed is null
     * @throws NoSuchElementException   if there isn't such assistant in the player's deck
     */
    Board playAssistant(AssistantType type) throws IllegalArgumentException, NoSuchElementException {
        if (type == null) throw new IllegalArgumentException("Assistant type must not be null.");

        Board b = new Board(this);
        Assistant a = getAssistant(type);
        b.lastPlayed = a;
        b.assistants.remove(a);
        return b;
    }

    /**
     * Creates another Player instance with the specified deck of {@link Assistant}s, and it returns the new Player..
     *
     * @param assistantDeck the given deck of assistants
     * @return the new Player instance
     * @throws IllegalStateException    if {@code deckAdded == true} (can't add a new deck if one is already present)
     * @throws IllegalArgumentException if {@code assistants == null}
     */
    Board receiveDeck(List<Assistant> assistantDeck) throws IllegalArgumentException, IllegalStateException {
        if (assistantDeck == null)
            throw new IllegalArgumentException("Assistant deck must not be null.");
        if (deckAdded)
            throw new IllegalStateException("A deck for this player has already been added.");

        Board b = new Board(this);
        b.assistants = assistantDeck;
        b.deckAdded = true;
        return b;
    }

    /**
     * Player getter.
     * @return the player who owns this board
     */
    private Player getPlayer() {
        return player;
    }

    /**
     * {@code lastPlayedAssistant} getter.
     *
     * @return an {@code Optional of Assistant} (because initially {@code lastPlayedAssistant} could be {@code null})
     */
    public Optional<Assistant> getLastPlayedAssistant() {
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
     * {@link Board#towers}. This method then returns another Board instance with the updated internal state.
     *
     * @return the new Board instance
     * @throws IllegalStateException    if {@link Board#towers} is empty
     * @throws IllegalArgumentException if the Consumer passed is null
     */
    Board sendTower(Consumer<Tower> consumer) throws IllegalArgumentException, IllegalStateException {
        if (consumer == null) throw new IllegalArgumentException("Consumer must not be null.");
        if (towers.size() == 0) throw new IllegalStateException("The player has no towers.");

        Board b = new Board(this);
        consumer.accept(b.towers.pop());
        return b;
    }

    /**
     * The player receives one {@link Tower}. The method then returns another Board instance, containing that
     * tower.
     *
     * @throws IllegalArgumentException if {@code tower == null}
     * @throws IllegalArgumentException if tower's color is incorrect
     * @throws IllegalStateException    if {@code towers} is full
     */
    Board receiveTower(Tower t) throws IllegalArgumentException {
        if (t == null)
            throw new IllegalArgumentException("Tower must not be null.");
        if (!t.getColor().equals(towersColor))
            throw new IllegalArgumentException("The tower parameter has a different color from the player's towers.");
        if (towers.size() == maxNumTowers)
            throw new IllegalStateException("This player already has the maximum number of towers in his board.");

        Board b = new Board(this);
        b.towers.add(t);
        return b;
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
     * of the original Board instance, with the applied update.
     *
     * @param update the function to apply
     * @return the new Board instance
     * @throws IllegalArgumentException if the function passed is null
     */
    Board updateHall(Function<Hall, Hall> update) throws IllegalArgumentException {
        if (update == null)
            throw new IllegalArgumentException("Update must not be null.");

        Board b = new Board(this);
        b.hall = update.apply(b.hall);
        return b;
    }

    /**
     * This method applies an update to the player's entrance, which is expressed via a
     * {@code Function<BoundedContainer, BoundedContainer>}, passed via parameter. This method then returns a copy
     * of the original Board instance, with the applied update.
     *
     * @param update the function to apply
     * @return the new Board instance
     * @throws IllegalArgumentException if the function passed is null
     */
    Board updateEntrance(Function<BoundedStudentContainer, BoundedStudentContainer> update) throws IllegalArgumentException {
        if (update == null)
            throw new IllegalArgumentException("Update must not be null.");

        Board b = new Board(this);
        b.entrance = update.apply(entrance);
        return b;
    }

    // Coins management

    /**
     * The player spends {@code num_coins} coins. This method then returns a new Board instance with the updated
     * internal state
     *
     * @param numCoins the number of coins to be spent
     * @throws IllegalArgumentException if {@code numCoins <= 0}
     * @throws IllegalStateException    if {@code coins - numCoins <= 0} (the player tries to spend coins it doesn't have)
     */
    Board spendCoins(int numCoins) throws IllegalArgumentException, IllegalStateException {
        if (numCoins <= 0) throw new IllegalArgumentException("The number of coins spent cannot be less than 1");
        if (numCoins > coins) throw new IllegalStateException("Not enough coins.");

        Board b = new Board(this);
        b.coins -= numCoins;
        return b;
    }

    /**
     * The player receives one coin. A new Board instance with the updated internal state is then returned.
     *
     * @return the new Board instance
     */
    Board receiveCoin() throws IllegalArgumentException {
        Board b = new Board(this);
        b.coins++;
        return b;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return player.equals(board.player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(player);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Board{" +
                "player=" + player +
                ", assistants=" + assistants +
                ", lastPlayed=" + lastPlayed +
                ", entrance=" + entrance +
                ", hall=" + hall +
                ", towers=" + towers +
                ", maxNumTowers=" + maxNumTowers +
                ", towersColor=" + towersColor +
                ", coins=" + coins +
                ", deckAdded=" + deckAdded +
                '}';
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