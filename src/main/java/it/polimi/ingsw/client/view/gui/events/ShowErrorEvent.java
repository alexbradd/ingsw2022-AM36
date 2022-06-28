package it.polimi.ingsw.client.view.gui.events;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * Event fired at the root element when an error should be shown.
 */
public class ShowErrorEvent extends Event {
    /**
     * Event type
     */
    public static final EventType<ShowErrorEvent> ERROR = new EventType<>("ERROR");

    /**
     * The error text to show.
     */
    private final String errorText;

    /**
     * Creates a new instance of the event with the given properties.
     *
     * @param eventType the event type
     * @param errorText the text that will be shown
     */
    public ShowErrorEvent(EventType<? extends Event> eventType, String errorText) {
        super(eventType);
        this.errorText = errorText;
    }

    /**
     * Getter for the text that the error will show.
     *
     * @return the text that the error will show.
     */
    public String getErrorText() {
        return errorText;
    }
}
