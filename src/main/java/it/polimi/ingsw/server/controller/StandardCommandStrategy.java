package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.polimi.ingsw.ProgramOptions;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.controller.commands.UserCommandType;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.PhaseDiff;
import it.polimi.ingsw.server.net.Dispatcher;

import static it.polimi.ingsw.server.controller.CommandUtilities.addPlayer;
import static it.polimi.ingsw.server.controller.CommandUtilities.removePlayer;
import static it.polimi.ingsw.server.controller.Messages.buildErrorMessage;
import static it.polimi.ingsw.server.controller.Messages.buildUpdateMessage;

/**
 * This class represents the standard concrete {@link CommandStrategy} to use for the managing of a {@link UserCommand}
 * (if the match is not in a {@code rejoining} state).
 *
 * @author Leonardo Bianconi
 * @see CommandStrategy
 */
public class StandardCommandStrategy implements CommandStrategy {
    /**
     * Manages a command on a match. It asks the model instance ({@link Game} instance) for the changing of its state
     * and:
     * <ul>
     *     <li>If the changes bring the {@code Game} to a legal state, apply it and send the {@code UPDATE} message in
     *     broadcast to the players that are connected to that {@code Game}</li>
     *     <li>If the update is invalid, notify the player with a {@code ERROR} message</li>
     * </ul>
     *
     * @param command the {@link UserCommand} to manage
     * @param match   the {@link Match} on which to apply the command
     */
    @Override
    public void manageCommand(Tuple<UserCommand, Dispatcher> command, Match match) {
        UserCommandType type = command.getFirst().getType();
        String username = command.getFirst().getUsername();
        Game g = match.getGame();
        Dispatcher sender = command.getSecond();

        PhaseDiff diff;
        try {
            diff = g.executeUserCommand(command.getFirst());
        } catch (Exception exc) {
            sender.send(buildErrorMessage(match.getId(), exc.getMessage()));
            return;
        }
        diff.addAttribute("cause", new JsonPrimitive(command.getFirst().getModificationMessage()));

        if (ProgramOptions.usesPersistence())
            MatchRegistry.getInstance()
                    .getPersistenceManager()
                    .commit(match.getId(), match.getGame().getPhase());

        switch (type) {
            case JOIN -> {
                try {
                    addPlayer(sender, username, match);
                } catch (IllegalArgumentException e) {
                    sender.send(buildErrorMessage(match.getId(), e.getMessage()));
                    return;
                }
            }
            case LEAVE -> removePlayer(sender, username, match);
        }
        JsonObject update = buildUpdateMessage(diff.toJson().getAsJsonObject(), match.getId());
        match.sendBroadcast(update);
    }
}
