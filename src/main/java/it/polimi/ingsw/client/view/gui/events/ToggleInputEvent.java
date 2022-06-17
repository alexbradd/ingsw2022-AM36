package it.polimi.ingsw.client.view.gui.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Event fired at the root element when control should be given/taken away from the player.
 */
public class ToggleInputEvent extends Event {
    /**
     * EventType for an event that signals that the application should inhibit user input
     */
    public static final EventType<ToggleInputEvent> DISABLE = new EventType<>("DISABLE");
    /**
     * EventType for an event that signals that the application should enable user input
     */
    public static final EventType<ToggleInputEvent> ENABLE = new EventType<>("ENABLE");

    /**
     * Creates a new instance with the given event type
     *
     * @param eventType the event type
     */
    public ToggleInputEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }
}
