package it.polimi.ingsw.server.controller;

import com.google.gson.JsonObject;
import it.polimi.ingsw.functional.Tuple;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.controller.commands.UserCommandType;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.model.PhaseDiff;
import it.polimi.ingsw.server.net.Dispatcher;

import java.util.ArrayList;

/**
 * This class implements a task that, until termination, takes match-related commands from the {@link #match} command
 * queue and executes them on the related {@link Game} instance. It then returns the response (in broadcast or to the
 * sender {@link Dispatcher}).
 * This task is meant to be executed on a separate thread from the main thread (that handles the {@link MatchRegistry}),
 * and every Match instance should have one (and only one) CommandManager related to it.
 *
 * @author Leonardo Bianconi
 * @see Match
 * @see Game
 */
public class CommandManager implements Runnable {
    /**
     * The corresponding {@link Match} instance.
     */
    private final Match match;

    /**
     * The default constructor.
     *
     * @param match the {@link Match} instance to be bound to
     */
    CommandManager(Match match) {
        this.match = match;
    }

    /**
     * Main operation, repeated until thread termination. Manages the commands in the commands queue. If the queue has
     * at least one element, it takes (blocking method synchronizing on the queue) the first element and executes the
     * {@link #manageCommand(Tuple)} method.
     */
    @Override
    public void run() {
        try {
            while (true) {
                manageCommand(match.getCommands().take());
                System.out.println("command taken...");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for the management of a single command of the queue. It executes it on the {@link #match}'s {@link Game}
     * instance; then, it does the following:
     * <ul>
     *     <li>If there's an exception thrown inside the model, return a {@code ERR} message to the sender's
     *          {@link Dispatcher}</li>
     *     <li>Else, return a {@code UPDATE} message in broadcast</li>
     * </ul>
     *
     * @param command a Tuple representing an element of the queue of commands ({@link UserCommand}, {@link Dispatcher})
     */
    void manageCommand(Tuple<UserCommand, Dispatcher> command) {
        Dispatcher sender = command.getSecond();
        Game g = match.getGame();
        PhaseDiff diff;
        JsonObject res;

        try {
            diff = g.executeUserCommand(command.getFirst());
            res = diff.toJson().getAsJsonObject();

        } catch (Exception exc) {
            res = Messages.buildErrorMessage(match.getId(), exc.getMessage());
            sender.send(res);
        }

        UserCommandType type = command.getFirst().getType();
        if (type.equals(UserCommandType.JOIN))
            match.addDispatcher(sender);

        else if (type.equals(UserCommandType.LEAVE)) {
            match.removeDispatcher(sender);
            sender.send(Messages.buildLeftMessage(match.getId()));
        }

        for (Dispatcher d : new ArrayList<>(match.getDispatchers()))
            d.send(res);
    }

    /**
     * Getter for the {@code match}.
     *
     * @return the {@code Match} the manager is related to
     */
    Match getMatch() {
        return match;
    }
}


