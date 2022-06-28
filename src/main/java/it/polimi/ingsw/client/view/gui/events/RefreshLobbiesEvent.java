package it.polimi.ingsw.client.view.gui.events;

import it.polimi.ingsw.client.control.state.Lobby;
import javafx.event.Event;
import javafx.event.EventType;

import java.util.List;

/**
 * Event fired at the root element when a new list of lobbies becomes available
 */
public class RefreshLobbiesEvent extends Event {
    /**
     * Event type
     */
    public static final EventType<RefreshLobbiesEvent> REFESH = new EventType<>("REFRESH");
    /**
     * The list of lobbies fetched
     */
    private final List<Lobby> lobbies;

    /**
     * Creates a new instance with the given type and list of lobbies
     *
     * @param eventType the event type
     * @param lobbies   the list of {@link Lobby} fetched
     * @throws IllegalArgumentException if {@code lobbies} is null
     */
    public RefreshLobbiesEvent(EventType<? extends Event> eventType, List<Lobby> lobbies) {
        super(eventType);
        if (lobbies == null) throw new IllegalArgumentException("lobbies shouldn't be null");
        this.lobbies = lobbies;
    }

    /**
     * Getter for the fetched list of lobbies
     *
     * @return the fetched list of lobbies
     */
    public List<Lobby> getLobbies() {
        return lobbies;
    }
}
