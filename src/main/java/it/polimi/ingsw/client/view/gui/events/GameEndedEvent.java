package it.polimi.ingsw.client.view.gui.events;

import javafx.event.Event;
import javafx.event.EventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Event fired at the root element when the game has ended due to an END command.
 */
public class GameEndedEvent extends Event {
    /**
     * Event type for when the game has ended for a generic reason
     */
    public static final EventType<GameEndedEvent> END = new EventType<>("END");
    /**
     * Event type for when the game has ended due to a player winning
     */
    public static final EventType<GameEndedEvent> WIN = new EventType<>("WIN");

    /**
     * The message to show
     */
    private final String endGameText;
    /**
     * The list of usernames that won
     */
    private final List<String> winners;

    /**
     * Creates a new instance of the event with the given properties. The winner list is initialized as an empty list.
     *
     * @param eventType   the event type
     * @param endGameText the reason why the game ended
     */
    public GameEndedEvent(EventType<? extends Event> eventType, String endGameText) {
        super(eventType);
        this.endGameText = endGameText;
        this.winners = List.of();
    }

    /**
     * Creates a new instance of the event with the given properties.
     *
     * @param eventType   the event type
     * @param endGameText the reason why the game ended
     * @param winners     a list containing the usernames of the player that won
     */
    public GameEndedEvent(EventType<? extends Event> eventType, String endGameText, List<String> winners) {
        super(eventType);
        this.endGameText = endGameText;
        this.winners = new ArrayList<>(winners);
    }

    /**
     * Getter for the reason the game ended
     *
     * @return the reason the game ended
     */
    public String getEndGameText() {
        return endGameText;
    }

    /**
     * Returns the list usernames that won
     *
     * @return the list usernames that won
     */
    public List<String> getWinners() {
        return winners;
    }
}
