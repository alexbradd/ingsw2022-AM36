package it.polimi.ingsw.server.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.polimi.ingsw.server.controller.commands.Parser;
import it.polimi.ingsw.server.controller.commands.UserCommand;
import it.polimi.ingsw.server.model.Game;
import it.polimi.ingsw.server.net.Dispatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * This class keeps track of all the matches that are currently active. It is a singleton. It manages all the
 * game-related {@link UserCommand}s in the following way:
 * <ul>
 *     <li>if {@code type == "FETCH"} return all the currently active matches</li>
 *     <li>if {@code type == "CREATE"}, create a new {@link Match} and then send a new {@code JOIN} command to the
 *      {@link Match}</li>
 *     <li>else, let the {@link Match} instance manage the command</li>
 * </ul>
 *
 * @author Leonardo Bianconi
 * @see Match
 * @see UserCommand
 * @see Dispatcher
 */
public class MatchRegistry {
    /**
     * The concrete {@code GameRegistry} instance.
     */
    private static MatchRegistry registryInstance = null;
    /**
     * The list of currently active {@link Match}es on this server.
     */
    private final List<Match> matches;

    /**
     * A supplier of {@link Match}es that is called every time a new Match has to be created. In production environment,
     * a normal {@link Match} instance should be returned, while other implementation of the Match class have been used
     * during tests (see related tests).
     */
    private final BiFunction<Integer, Game, Match> matchSupplier;

    /**
     * Default constructor.
     */
    private MatchRegistry(BiFunction<Integer, Game, Match> matchSupplier) {
        this.matches = new ArrayList<>();
        this.matchSupplier = matchSupplier;
    }

    /**
     * Getter for the {@code GameRegistry} instance. It allows to specify a {@link #matchSupplier}. Note that this is
     * possible only when the {@code GameRegistry} is instantiated, otherwise, this method returns the previously
     * instantiated {@code GameRegistry}.
     *
     * @param matchSupplier the supplier for new {@code Match}es
     * @return the {@code GameRegistry} instance
     */
    public static MatchRegistry getInstance(BiFunction<Integer, Game, Match> matchSupplier) {
        if (registryInstance == null) registryInstance = new MatchRegistry(matchSupplier);
        return registryInstance;
    }

    /**
     * Getter for the {@code GameRegistry} instance. If the singleton is not yet instantiated, this method instantiates
     * it with the default {@link Match#Match(int, Game)} as {@link #matchSupplier}.
     *
     * @return the {@code GameRegistry} instance
     */
    public static MatchRegistry getInstance() {
        if (registryInstance == null) registryInstance = new MatchRegistry(Match::new);
        return registryInstance;
    }

    /**
     * This method manages a command, expressed as a JSON object (gson {@code JsonObject}), and decides whether to
     * process the response directly (in case of a {@code FETCH} or {@code CREATE} command) or to route it to a specific
     * {@link Match} (in case of another {@code type} attribute (see net protocol docs).
     *
     * @param dispatcher  the {@link Dispatcher} instance that sent the command
     * @param jsonCommand the command, expressed as a JSON object
     * @see #fetchMatches(Dispatcher)
     * @see #createMatch(Dispatcher, JsonObject)
     * @see #sendCommandToMatch(Dispatcher, JsonObject)
     */
    public void executeCommand(Dispatcher dispatcher, JsonObject jsonCommand) {
        System.out.println("NEW COMMAND: " + jsonCommand.toString());

        String type = null;
        try {
            type = Messages.extractString(jsonCommand, "type");
        } catch (Exception e) {
            dispatcher.send(Messages.buildErrorMessage("Message has no 'type' attribute."));
        }

        assert type != null;
        switch (type) {
            case "PONG" -> dispatchPong(dispatcher, jsonCommand);
            case "FETCH" -> fetchMatches(dispatcher);
            case "CREATE" -> createMatch(dispatcher, jsonCommand);
            default -> sendCommandToMatch(dispatcher, jsonCommand);
        }
    }

    //executeCommand() helpers

    private void dispatchPong(Dispatcher dispatcher, JsonObject jsonCommand) {
        long gameId;
        try {
            gameId = Messages.extractNumber(jsonCommand, "type");
            get(gameId).getPinger().notifyResponse(dispatcher);

        } catch (NoSuchElementException e) {
        dispatcher.send(Messages.buildErrorMessage("Wrong game ID."));

        } catch (Exception e) {
            dispatcher.send(Messages.buildErrorMessage("Wrong PONG message format."));
        }
    }

    /**
     * Helper method for fetching all active {@link Match}es and sending back to the {@link Dispatcher} the formatted
     * response.
     *
     * @param dispatcher the player's {@link Dispatcher}
     */
    private void fetchMatches(Dispatcher dispatcher) {
        JsonObject res = new JsonObject();
        res.addProperty("type", "LOBBIES");

        JsonArray arr = new JsonArray();
        for (Match m : getAll())
            arr.add(m.toJson());

        res.add("lobbies", arr);
        dispatcher.send(res);
    }

    /**
     * Helper method that creates a new {@link Match} with the specific arguments passed inside the command. It then
     * creates a new {@code JOIN} command for the requesting player and executes it.
     *
     * @param dispatcher the player's {@link Dispatcher}
     * @param command    the {@code JsonObject} representing the {@code CREATE} command
     */
    private void createMatch(Dispatcher dispatcher, JsonObject command) {
        if (!isValidCreate(command))
            dispatcher.send(Messages.buildErrorMessage("Syntax error in the CREATE message."));

        JsonObject arguments = command.get("arguments")
                .getAsJsonArray()
                .get(0)
                .getAsJsonObject();

        boolean isExpertMode = arguments
                .get("expert")
                .getAsBoolean();

        int nPlayers = arguments
                .get("nPlayers")
                .getAsInt();

        int gameId = chooseGameId();

        JsonObject joinCommandObj = convertToJoin(command, gameId);
        Match newlyCreatedMatch = create(gameId, nPlayers, isExpertMode);

        dispatcher.setOnReceive(new InMatchCallback());
        dispatcher.setOnDisconnect(new DisconnectCallback(newlyCreatedMatch));
        executeCommand(dispatcher, joinCommandObj);
    }

    /**
     * Helper method for routing a command to the corresponding {@link Match} instance.
     *
     * @param dispatcher  the player's {@link Dispatcher}
     * @param jsonCommand the {@code JsonObject} representing the match-specific command
     */
    private void sendCommandToMatch(Dispatcher dispatcher, JsonObject jsonCommand) {
        try {
            UserCommand command = Parser.parse(jsonCommand);
            Match m = get(command.getGameId());
            m.executeUserCommand(command, dispatcher);

        } catch (IllegalArgumentException e) {
            dispatcher.send(Messages.buildErrorMessage(e.getMessage()));

        } catch (NoSuchElementException e) {
            dispatcher.send(Messages.buildErrorMessage("Wrong game ID."));
        }
    }

    /**
     * Helper method. Returns true if and only if the specified {@code JsonObject} command is a syntactically valid
     * {@code CREATE} JSON command.
     *
     * @param command the command to be tested
     * @return {@code true <==> command} is a valid {@code CREATE} JSON command
     * @see Messages
     */
    private boolean isValidCreate(JsonObject command) {
        String type;
        int nPlayers;
        try {
            type = Messages.extractString(command, "type");
            nPlayers = (int) Messages.extractNumber(command, "nPlayers");
            Messages.extractBoolean(command, "expertMode");
        } catch (Exception e) {
            return false;
        }

        return type.equals("CREATE") && (nPlayers == 2 || nPlayers == 3);
    }

    /**
     * Helper method that picks a game ID (unique identifier) for a new game. This implementation picks the smallest
     * positive (or zero) integer that hasn't already been chosen as a game ID.
     *
     * @return the game ID
     */
    private int chooseGameId() {
        List<Long> alreadyTaken = matches.stream()
                .mapToLong(Match::getId)
                .sorted()
                .boxed()
                .collect(Collectors.toList());

        int i = 0;
        while (i < alreadyTaken.size() && i == alreadyTaken.get(i))
            i++;

        return i;
    }

    /**
     * Helper method that converts a {@code CREATE} command into a {@code JOIN} command to the same
     * {@link Match} (both expressed as {@code JsonObject}).
     *
     * @param createCommand the command to be converted
     * @param gameId        the ID of the game to join
     * @return the converted command
     */
    private JsonObject convertToJoin(JsonObject createCommand, int gameId) {
        JsonObject joinCommand = createCommand.deepCopy();
        joinCommand.remove("type");
        joinCommand.remove("arguments");
        joinCommand.addProperty("type", "JOIN");
        joinCommand.addProperty("gameId", gameId);
        return joinCommand;
    }

    // getters

    /**
     * Getter for the {@link Match} with the specified ID.
     *
     * @param id the ID of the {@link Match}
     * @return the corresponding {@link Match} instance
     * @throws NoSuchElementException if there is no Match in the registry with the specified ID
     */
    Match get(long id) throws NoSuchElementException {
        for (Match m : getInstance().matches)
            if (m.getId() == id)
                return m;

        throw new NoSuchElementException("A match with the specified id does not exist.");
    }

    /**
     * Getter for all the {@link Match}es of the registry (shallow copy).
     *
     * @return the List<{@link Match}> of all matches
     */
    List<Match> getAll() {
        return new ArrayList<>(matches);
    }

    // Matches operations

    /**
     * Method for creating a new {@link Match} with the specified ID and game rules (number of players and expert mode).
     *
     * @param id           the ID of the Match
     * @param nPlayers     the number of players of the game
     * @param isExpertMode if the game is an expert mode game
     * @return the newly created Match
     */
    Match create(int id, int nPlayers, boolean isExpertMode) {
        Match m = matchSupplier.apply(id, new Game(nPlayers, isExpertMode));
        matches.add(m);     // FIXME: add synchronization for accessing matches
        return m;
    }

    /**
     * Method for killing the {@link Match} with the specified ID. Before that, all the match-termination operations
     * are performed.
     *
     * @param id the id of the {@link Match}
     * @throws NoSuchElementException if a {@link Match} with the corresponding ID does not exist in the registry
     */
    void terminate(long id) throws NoSuchElementException {
        Match m = get(id);

        for (Dispatcher d : m.getDispatchers()) {
            d.setOnDisconnect(null);
            m.removeDispatcher(d);
        }
        matches.remove(m);
    }

    /**
     * Method for killing the {@link Match} with the specified ID. An END message is sent to all connected dispatchers
     * with the specified reason.
     *
     * @param id     the id of the {@link Match}
     * @param reason a {@code String} that specifies why the {@link Match} has been terminated
     * @throws NoSuchElementException if a {@link Match} with the corresponding ID does not exist in the registry
     */
    void terminate(long id, String reason) throws NoSuchElementException {
        Match m = get(id);

        for (Dispatcher d : m.getDispatchers()) {
            d.setOnDisconnect(null);
            d.send(Messages.buildEndMessage(m.getId(), reason, new ArrayList<>()));
            m.removeDispatcher(d);
        }
        matches.remove(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "GameRegistry{" +
                "matches=" + matches +
                '}';
    }
}
