package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.net.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * This class represents a {@code TimerTask} that sends "PING" messages to all the {@link Dispatcher}s that are
 * connected to a certain {@link Match}. This class is immutable and relative to a single "wave" of PING/PONG message
 * exchanges (one PING to every connected client). This task is intended to be run periodically.
 *
 * @author Leonardo Bianconi
 * @see Match
 * @see Dispatcher
 */
public class Pinger extends TimerTask {
    /**
     * A {@code String} representing the reason of the possible termination of the {@link Match}.
     */
    private static final String TERM_REASON = "A player timed out.";
    /**
     * A list of dispatcher that hasn't responded to the ping message yet.
     */
    private final List<Dispatcher> dispatchers;
    /**
     * The {@link Match} instance this {@code Pinger} is relative to.
     */
    private final Match match;
    /**
     * The time (expressed in milliseconds) this thread waits for every PONG message to arrive.
     */
    private final long timeoutInMillis;

    /**
     * Default constructor.
     *
     * @param match the match this {@code Pinger} is relative to
     */
    public Pinger(Match match, long timeoutInMillis) {
        this.match = match;
        this.timeoutInMillis = timeoutInMillis;
        dispatchers = new ArrayList<>(match.getDispatchers());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    synchronized public void run() {
        System.out.println(this);

        for (Dispatcher d : dispatchers)
            d.send(Messages.buildPingMessage(match.getId()));

        try {
            wait(timeoutInMillis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!dispatchers.isEmpty()) {
            System.out.println("A player timed out. (match " + match.getId() + ")");
            MatchRegistry.getInstance().terminate(match.getId(), TERM_REASON);
        }
    }

    /**
     * This method notifies this {@code Pinger} that the specified {@code Dispatcher} has responded to the PING message.
     *
     * @param dispatcher the {@code Dispatcher} that sent the "PONG" message
     */
    synchronized public void notifyResponse(Dispatcher dispatcher) {
        dispatchers.remove(dispatcher);
    }

    @Override
    public String toString() {
        return "PING [MATCH: " + match.getId() + "]";
    }
}
