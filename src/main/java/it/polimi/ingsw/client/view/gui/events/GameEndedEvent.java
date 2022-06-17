package it.polimi.ingsw.client.view.gui.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Event fired at the root element when the game has ended due to a END command.
 */
public class GameEndedEvent extends Event {
    /**
     * Event type
     */
    public static final EventType<GameEndedEvent> END = new EventType<>("END");

    /**
     * The message to show
     */
    private final String endGameText;

    /**
     * Creates a new instance of the event with the given properties.
     *
     * @param eventType   the event type
     * @param endGameText the reason why the game ended
     */
    public GameEndedEvent(EventType<? extends Event> eventType, String endGameText) {
        super(eventType);
        this.endGameText = endGameText;
    }

    /**
     * Getter for the reason the game ended
     *
     * @return the reason the game ended
     */
    public String getEndGameText() {
        return endGameText;
    }
}
