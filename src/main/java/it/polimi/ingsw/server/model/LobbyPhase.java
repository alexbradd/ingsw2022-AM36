package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.TowerColor;
import it.polimi.ingsw.server.model.exceptions.InvalidPhaseUpdateException;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The LobbyPhase class represents the initial state of the game, in which Players are allowed to join and leave the game's lobby
 * through the {@code addPlayer()} and {@code removePlayer()} methods.
 * After the last Player joined the lobby, a new {@link PreparePhase} is returned.
 *
 * @author Mattia Busso
 * @see Phase
 */
public class LobbyPhase extends Phase {
    /**
     * A regex pattern that matches acceptable usernames. It corresponds to an alpha-numerical username between 1 and 30
     * characters long.
     */
    private final static Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9]{1,30}$");
    /**
     * A string describing the regex pattern used for validating usernames.
     */
    private final static String usernameRegexMsg = "pick a username between 1 and 30 characters long, that contains" +
            " only uppercase letters, lowercase letters and numbers";
    /**
     * The game's table.
     */
    private Table table;

    /**
     * Initial constructor.
     * Takes a new {@link GameParameters} instance as a parameter.
     *
     * @param parameters the game's parameters
     * @throws IllegalArgumentException if {@code parameters == null}
     */
    LobbyPhase(GameParameters parameters) throws IllegalArgumentException {
        super(parameters);
        table = new Table();
    }

    /**
     * Constructor that creates a copy of a {@code LobbyPhase} instance passed.
     *
     * @param old the old {@code LobbyPhase} instance
     * @throws IllegalArgumentException if {@code old.parameters == null}
     */
    LobbyPhase(LobbyPhase old) throws IllegalArgumentException {
        super(old);
        table = old.table;
    }

    // Adding a new player

    /**
     * {@inheritDoc}
     * @return a new and changed {@code LobbyPhase} instance or a new {@link PreparePhase} instance
     */
    @Override
    public Phase addPlayer(String username) throws IllegalArgumentException, InvalidPhaseUpdateException {
        if (username == null) {
            throw new IllegalArgumentException("username shouldn't be null");
        }
        if (table.getPlayers().stream().map(Player::getUsername).toList().contains(username)) {
            throw new InvalidPhaseUpdateException("a player with the same username is already taking part in the game");
        }
        if (table.getPlayers().size() == parameters.getnPlayers()) {
            throw new InvalidPhaseUpdateException("maximum number of players have already joined the game");
        }

        Matcher usernameMatcher = usernamePattern.matcher(username);
        if (!usernameMatcher.matches()) throw new InvalidPhaseUpdateException("Wrong username: " + usernameRegexMsg);

        Player newPlayer = new Player(username);
        LobbyPhase p = new LobbyPhase(this);
        p.table = p.table.addPlayer(newPlayer, parameters.getnStudentsEntrance(), parameters.getnTowers(), pickTowerColor());

        return p.table.getPlayers().size() < parameters.getnPlayers() ? p : new PreparePhase(p);

    }

    /**
     * Helper method that returns a {@code TowerColor} that has not already been picked by a player.
     *
     * @return a new {@code TowerColor}
     */
    private TowerColor pickTowerColor() {
        TowerColor pickedColor = TowerColor.BLACK;
        List<TowerColor> usedTowerColors = table.getBoards().stream().map(Board::getTowersColor).toList();

        for(TowerColor color : TowerColor.values()) {
            if(!usedTowerColors.contains(color)) {
                if(color != TowerColor.GRAY) {
                    pickedColor = color;
                    break;
                }
                else if(parameters.getnPlayers() != 2) {
                    pickedColor = color;
                    break;
                }
            }
        }

        return pickedColor;
    }

    // Removing a player

    /**
     * {@inheritDoc}
     * @return a new instance of {@code LobbyPhase}
     */
    @Override
    public Phase removePlayer(String username) throws InvalidPhaseUpdateException {
        if(username == null) {
            throw new IllegalArgumentException("username shouldn't be null");
        }
        if(!table.getPlayers().stream().map(Player::getUsername).toList().contains(username)) {
            throw new InvalidPhaseUpdateException("a player with the given username is not taking part in the game");
        }

        LobbyPhase p = new LobbyPhase(this);
        p.table = p.table.removePlayer(username);

        return p;
    }

    // Table getter

    /**
     * {@inheritDoc}
     */
    @Override
    Table getTable() {
        return table;
    }

    /**
     * Calculates a {@link PhaseDiff} from this LobbyPhase and the given one. All the entities of the passed Phase are
     * returned into the diff since this Phase does not guarantee that all players could be present.
     *
     * @param other the Phase to compare against
     * @return a new {@link PhaseDiff}
     * @throws IllegalArgumentException if any argument is null
     */
    @Override
    public PhaseDiff compare(Phase other) {
        return other.dump();
    }

    // equals and hash

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LobbyPhase that = (LobbyPhase) o;
        return Objects.equals(table, that.table) &&
                Objects.equals(parameters, that.parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(table, parameters);
    }
}
