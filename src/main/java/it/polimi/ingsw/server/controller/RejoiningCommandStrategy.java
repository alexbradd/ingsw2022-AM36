package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.controller.commands.UserCommandType;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.net.Dispatcher;

import static it.polimi.ingsw.server.controller.CommandUtilities.addPlayer;
import static it.polimi.ingsw.server.controller.CommandUtilities.removePlayer;
import static it.polimi.ingsw.server.controller.Messages.buildErrorMessage;
import static it.polimi.ingsw.server.controller.Messages.buildUpdateMessage;

/**
 * This class represents the concrete {@link CommandStrategy} to use for the managing of a {@link UserCommand} if the
 * match is in {@code rejoining} state (i.e. the match has been restored from disk and the server is waiting for the
 * previously connected players to join).
 *
 * @author Leonardo Bianconi
 * @see CommandStrategy
 */
public class RejoiningCommandStrategy implements CommandStrategy {
    /**
     * Manages a command on a match when it is in {@code rejoining} state. It only lets re-join players with a username
     * that matches one stored inside the {@link Game}'s phase.
     *
     * @param command the {@link UserCommand} to manage
     * @param match   the {@link Match} on which to apply the command
     */
    @Override
    public void manageCommand(Tuple<UserCommand, Dispatcher> command, Match match) {
        System.out.println("using rejoining strategy...");

        UserCommandType type = command.getFirst().getType();
        String username = command.getFirst().getUsername();
        Game game = match.getGame();
        Dispatcher sender = command.getSecond();

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
            default -> {
                sender.send(buildErrorMessage(match.getId(),
                        "This match is in rejoining state. Only JOIN and LEAVE commands allowed."));
                return;
            }
        }

        JsonObject dump = game.dumpPhase().toJson().getAsJsonObject();
        JsonObject update = buildUpdateMessage(dump, match.getId(), match.isRejoiningState(), match.getMissingPlayers());
        match.sendBroadcast(update);
    }
}
