package it.polimi.ingsw.server.model;

import it.polimi.ingsw.server.model.enums.AssistantType;
import it.polimi.ingsw.server.model.enums.TowerColor;
import java.util.*;
import java.util.List;

/**
 * *STUB*
 *
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
public class Player {

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
        if(entranceSize <= 0) {
            throw new IllegalArgumentException("entranceSize shouldn't be <= 0");
        }
        username = name;
        assistants = new ArrayList<>();
        towers = new Stack<>();
        maxNumTowers = numTowers;
        towersColor = color;
        for(int i = 0; i < numTowers; i++) {
            towers.add(new Tower(color, this));
        }
    }

    Player(Player oldPlayer) {
        if(oldPlayer == null) {
            throw new IllegalArgumentException("old Player shouldn't be null");
        }
        this.username = oldPlayer.username;
        this.assistants = new ArrayList<>(oldPlayer.assistants);
        this.towers = (Stack<Tower>) oldPlayer.towers.clone();
        this.maxNumTowers = oldPlayer.maxNumTowers;
        this.towersColor = oldPlayer.towersColor;
        this.lastPlayed = oldPlayer.lastPlayed;
    }

    // Assistant's deck management

    Player receiveDeck(List<Assistant> assistants) {
        if(assistants == null) {
            throw new IllegalArgumentException("assistants shouldn't be null");
        }
        this.assistants = assistants;
        return new Player(this);
    }

    Player playAssistant(AssistantType type) {
        for(Assistant assistant: assistants) {
            if(assistant.getOrderValue() == type.getValue()) {
                lastPlayed = assistant;
            }
        }
        assistants.remove(lastPlayed);
        return new Player(this);
    }

    /**
     * {@code lastPlayedAssistant} getter.
     *
     * @return an {@code Optional of Assistant} (because initially {@code lastPlayedAssistant} could be {@code null})
     */
    public Optional<Assistant> getLastPlayedAssistant() {
        return Optional.ofNullable(lastPlayed);
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

    // Hall and entrance updates

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

}
