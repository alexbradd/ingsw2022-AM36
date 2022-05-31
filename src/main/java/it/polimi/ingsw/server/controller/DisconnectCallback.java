package it.polimi.ingsw.server.controller;

import it.polimi.ingsw.server.net.Dispatcher;

/**
 * This class represents the callback function to be called after a {@link Dispatcher} connected to a {@link Match}
 * disconnects. It kills the {@code Match} in which the disconnected player had taken part and notifies all the other
 * players.
 *
 * @author Leonardo Bianconi
 * @see Dispatcher
 * @see MatchRegistry
 * @see Match
 */
public class DisconnectCallback implements Runnable {
    /**
     * The {@code Match} the {@code Dispatcher} joined.
     */
    private final Match match;
    /**
     * The reason for killing the {@code Match}.
     */
    private final static String TERM_REASON = "A player disconnected.";

    /**
     * Default constructor.
     *
     * @param match the {@link Match} instance
     */
    public DisconnectCallback(Match match) {
        this.match = match;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        MatchRegistry.getInstance().terminate(match.getId(), TERM_REASON);
    }
}
