package it.polimi.ingsw.client.control.state;

import it.polimi.ingsw.enums.AssistantType;
import it.polimi.ingsw.enums.Mage;
import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.enums.TowerColor;

import java.util.Arrays;

/**
 * Client's representation of game's Board object.
 *
 * @author Mattia Busso
 */
public class Board {

    /**
     * The player who owns the board.
     */
    private String username;

    /**
     * The board's entrance.
     */
    private PieceColor[] entrance;

    /**
     * The player's assistants.
     */
    private AssistantType[] assistants;

    /**
     * The player's mage.
     */
    private Mage mage;

    /**
     * The board's hall.
     */
    private PieceColor[] hall;

    /**
     * The board's towers.
     */
    private TowerColor[] towers;

    /**
     * The player's last played assistant.
     */
    private AssistantType lastPlayedAssistant;

    /**
     * The player's coins.
     */
    private int coins;

    // getters

    /**
     * Returns the entrance of the player.
     *
     * @return the entrance of the player
     */
    public PieceColor[] getEntrance() {
        return entrance;
    }

    /**
     * Returns the player's mage
     * @return the player's mage
     */
    public Mage getMage() {
        return mage;
    }

    /**
     * Returns the deck of assistants of the player
     * @return the assistants of the player
     */
    public AssistantType[] getAssistants() {
        return assistants;
    }

    /**
     * Returns the username of the player
     * @return the username of the player
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the last played assistant by the player
     * @return the last played assistant by the player
     */
    public AssistantType getLastPlayedAssistant() {
        return lastPlayedAssistant;
    }

    /**
     * Returns the number of coins of the player.
     * @return the number of coins of the player
     */
    public int getCoins() {
        return coins;
    }

    // stringify

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return  (entrance != null && towers != null ? "   " + "\"" + username + "\"\n" +
                "      Entrance: " + Arrays.toString(entrance) + "\n" +
                "      Towers: " + Arrays.toString(towers) + "\n" : "") +
                (hall == null ? "" : "      Hall: " + countHallColors() + "\n") +
                (assistants == null ? "" : "      Assistants: " + Arrays.toString(assistants) + "\n") +
                (mage == null ? "" : "      Mage: " + mage + "\n") +
                (lastPlayedAssistant == null ? "" : "      Last played assistant: " + lastPlayedAssistant + "\n") +
                (coins == 0 ? "" : "      Coins: " + coins + "\n");
    }

    /**
     * Helper method that returns a color-frequency custom formatted string for the hall.
     *
     * @return the custom formatted string
     */
    private String countHallColors() {
        StringBuilder s = new StringBuilder();

        for(PieceColor color : PieceColor.values()) {
            int count = 0;
            for (PieceColor student : hall) {
                if (student.equals(color)) count++;
            }
            s.append(count == 0 ? "" : " " + count + "x" + color);
        }

        return s.toString();
    }

}
