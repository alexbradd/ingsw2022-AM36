package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.net.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * // TODO: docs
 */
public class Pinger extends TimerTask {
    private final List<Dispatcher> dispatchers;
    private final Match match;

    public Pinger(List<Dispatcher> dispatchers, Match match) {
        this.match = match;
        this.dispatchers = dispatchers;
    }

    @Override
    public void run() {
        for (Dispatcher d : dispatchers)
            d.send(Messages.buildPingMessage(match.getId()));
        System.out.println("PING");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!dispatchers.isEmpty()) {
            System.out.println("A player timed out");
            for (Dispatcher d : dispatchers)
                sendEndMessage(d);

            MatchRegistry.getInstance().terminate(match.getId());
        }
    }

    public void notifyResponse(Dispatcher dispatcher) {
        dispatchers.remove(dispatcher);
    }

    private void sendEndMessage(Dispatcher d) {
        JsonObject endMessage = Messages.buildEndMessage(match.getId(), "A player timed out.", new ArrayList<>());
        d.send(endMessage);
    }
}
