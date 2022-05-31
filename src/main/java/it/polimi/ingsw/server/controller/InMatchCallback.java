package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.server.net.Dispatcher;

import java.util.function.BiConsumer;

/**
 * This class represents a {@code BiConsumer}<{@link JsonObject}, {@link Dispatcher}> to be set to a {@link Dispatcher}
 * (through {@link Dispatcher#setOnReceive(BiConsumer)}) when the {@link Dispatcher} is connected to a {@link Match}.
 * It makes the Dispatcher return an error message if the player tries to send another {@code CREATE} or {@code JOIN}
 * message.
 *
 * @author Leonardo Bianconi
 * @see Dispatcher
 */
public class InMatchCallback implements BiConsumer<JsonObject, Dispatcher> {

    /**
     * The callback function.
     *
     * @param jsonObject the {@code JsonObject} to manage
     * @param dispatcher the {@link Dispatcher} instance
     */
    @Override
    public void accept(JsonObject jsonObject, Dispatcher dispatcher) {
        String type;
        try {
            type = Messages.extractString(jsonObject, "type");
        } catch (IllegalArgumentException e) {
            dispatcher.send(Messages.buildErrorMessage("Message has no 'type' attribute."));
            return;
        }

        if (type.equals("CREATE") || type.equals("JOIN")) {
            dispatcher.send(Messages.buildErrorMessage("Already inside a game on this client."));
            return;
        }

        MatchRegistry.getInstance().executeCommand(dispatcher, jsonObject);
    }
}
