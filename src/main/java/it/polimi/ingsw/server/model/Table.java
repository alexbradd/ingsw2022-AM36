package it.polimi.ingsw.server.model;

import it.polimi.ingsw.enums.PieceColor;
import it.polimi.ingsw.enums.TowerColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;

/**
 * Represents a snapshot of the game's playing board, including players. This is essentially a data holder manipulated
 * by the state machine.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Table {
    /**
     * The starting number of islands on the table.
     */
    private static final int START_ISLANDS = 12;

    /**
     * The starting number of Character cards on the table
     */
    private static final int START_CHARACTERS = 3;

    /**
     * The starting size of the list of players
     */
    private static final int START_PLAYERS = 3;

    /**
     * The sack (a simple StudentContainer).
     */
    private StudentContainer sack;

    /**
     * The List<{@link Cloud}> instance. It represents the clouds on the play area.
     */
    private List<Cloud> clouds;

    /**
     * The {@link MotherNature} instance.
     */
    private MotherNature motherNature;

    /**
     * The List<{@link Island}> instance. It represents the islands on the play area.
     */
    private List<Island> islandList;

    /**
     * A List containing all the {@link Professor} instances.
     */
    private List<Professor> professors;

    /**
     * A List containing all the {@link Character} instances (i.e. chosen characters for the game, only in
     * expert mode, see game rules).
     */
    private List<Character> characters;

    /**
     * The list of all players seated at the table, ordered in clockwise order.
     */
    private List<Player> playerList;

    /**
     * The lists of all the boards of all the players in the game
     */
    private List<Board> boardList;

    /**
     * Creates a new Table. Every list/container is initialized and empty except for the islands and professors. Mother
     * Nature is also placed at random on the list of islands.
     */
    Table() {
        this.sack = new StudentContainer();
        this.clouds = new ArrayList<>(START_PLAYERS);
        this.islandList = new ArrayList<>(START_ISLANDS);
        for (int i = 0; i < START_ISLANDS; i++)
            this.islandList.add(new Island(i));
        int mnStartingPos = new Random().nextInt(START_ISLANDS);
        this.motherNature = new MotherNature(this.islandList, mnStartingPos);
        this.professors = new ArrayList<>(PieceColor.values().length);
        for (PieceColor c : PieceColor.values())
            this.professors.add(new Professor(c));
        this.characters = new ArrayList<>(START_CHARACTERS);
        this.playerList = new ArrayList<>(START_PLAYERS);
        this.boardList = new ArrayList<>(START_PLAYERS);
    }

    /**
     * Creates a new Table that is the shallow copy of the given one.
     *
     * @param old the Table to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    Table(Table old) {
        if (old == null) throw new IllegalArgumentException("old cannot be null");
        this.sack = old.sack;
        this.clouds = old.clouds;
        this.motherNature = old.motherNature;
        this.islandList = old.islandList;
        this.professors = old.professors;
        this.characters = old.characters;
        this.playerList = old.playerList;
        this.boardList = old.boardList;
    }

    /**
     * Getter for the sack stored.
     *
     * @return the sack stored
     */
    StudentContainer getSack() {
        return sack;
    }

    /**
     * Applies the given update to the Sack stored.
     *
     * @param update the update to apply
     * @return a new Table with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Table updateSack(Function<StudentContainer, StudentContainer> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Table r = new Table(this);
        StudentContainer s = update.apply(r.sack);
        if (s != null)
            r.sack = s;
        return r;
    }

    /**
     * Getter for the list of Cloud stored.
     *
     * @return a duplicate of the list of Cloud stored
     */
    List<Cloud> getClouds() {
        return new ArrayList<>(clouds);
    }

    /**
     * Applies the given update to the list of Cloud stored. If the update returns null, it is discarded.
     *
     * @param update the update to apply
     * @return a new Table with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Table updateClouds(Function<List<Cloud>, List<Cloud>> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Table r = new Table(this);
        List<Cloud> c = applyToDuplicate(r.clouds, update);
        if (c != null)
            r.clouds = c;
        return r;
    }

    /**
     * Private utility that passes a duplicate of {@code list} to {@code update} if the first is not null. If
     * {@code list} is null, null is passed to the update.
     *
     * @param list   the list to update
     * @param update the update to apply
     * @param <T>    generic type
     * @return a new list with the update applied
     */
    private <T> List<T> applyToDuplicate(List<T> list, Function<List<T>, List<T>> update) {
        return update.apply(new ArrayList<>(list));
    }

    /**
     * Getter for the MotherIsland stored
     *
     * @return the MotherIsland stored
     */
    MotherNature getMotherNature() {
        return motherNature;
    }

    /**
     * Applies the given update to the MotherNature stored.
     *
     * @param update the update to apply
     * @return a new Table with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Table updateMotherNature(Function<MotherNature, MotherNature> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Table r = new Table(this);
        MotherNature m = update.apply(r.motherNature);
        if (m != null)
            r.motherNature = m;
        return r;
    }

    /**
     * Getter for the list of Island stored.
     *
     * @return a duplicate of the list of Island stored
     */
    List<Island> getIslandList() {
        return new ArrayList<>(islandList);
    }

    /**
     * Applies the given update to the list of Island stored. If the update returns null, it is discarded.
     *
     * @param update the update to apply
     * @return a new Table with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Table updateIslandList(Function<List<Island>, List<Island>> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Table r = new Table(this);
        List<Island> i = applyToDuplicate(r.islandList, update);
        if (i != null)
            r.islandList = i;
        return r;
    }

    /**
     * Getter for the list of Professor stored.
     *
     * @return a duplicate of the list of Professor stored
     */
    List<Professor> getProfessors() {
        return new ArrayList<>(professors);
    }

    /**
     * Applies the given update to the list of Professor stored. If the update returns null, the update is discarded.
     *
     * @param update the update to apply
     * @return a new Table with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Table updateProfessors(Function<List<Professor>, List<Professor>> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Table r = new Table(this);
        List<Professor> p = applyToDuplicate(r.professors, update);
        if (p != null)
            r.professors = p;
        return r;
    }

    /**
     * Getter for the list of Character stored.
     *
     * @return a duplicate of the list of Character stored
     */
    List<Character> getCharacters() {
        return new ArrayList<>(characters);
    }

    /**
     * Applies the given update to the list of Character stored. If the update returns null, it is discarded.
     *
     * @param update the update to apply
     * @return a new Table with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Table updateCharacters(Function<List<Character>, List<Character>> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Table r = new Table(this);
        List<Character> c = applyToDuplicate(r.characters, update);
        if (c != null)
            r.characters = c;
        return r;
    }

    /**
     * Getter for the list of Player stored.
     *
     * @return a duplicate of the list of Player stored
     */
    List<Player> getPlayers() {
        return new ArrayList<>(playerList);
    }

    /**
     * Seat the given {@link Player} at this table. Together with the Player a {@link Board} is added.
     *
     * @param player       this Player
     * @param entranceSize the entrance size of the Player's Board
     * @param numTowers    the number of Towers in this Player's Board
     * @param towerColor   the color of this player's Towers
     * @return a new Table with the new changes
     * @throws IllegalArgumentException if any parameter is null
     * @throws IllegalArgumentException if {@code entranceSize} and {@code numTowers} are less than or equal to zero
     * @throws IllegalArgumentException if a player with the same username is already seated at the table
     */
    Table addPlayer(Player player, int entranceSize, int numTowers, TowerColor towerColor) {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (entranceSize <= 0) throw new IllegalArgumentException("entranceSize should be > 0");
        if (numTowers <= 0) throw new IllegalArgumentException("numTowers should be > 0");
        if (towerColor == null) throw new IllegalArgumentException("towerColor shouldn't be null");
        if (playerList.stream().anyMatch(p -> Objects.equals(p.getUsername(), player.getUsername())))
            throw new IllegalArgumentException("A player with the given username is already in the list");
        Table t = new Table(this);
        t.playerList = new ArrayList<>(t.playerList);
        t.boardList = new ArrayList<>(t.boardList);
        t.playerList.add(player);
        t.boardList.add(new Board(player, entranceSize, numTowers, towerColor));
        return t;
    }

    /**
     * Remove the Player with the specified username and its Board  from this Table. If no matching Player can be found,
     * nothing is done.
     *
     * @param username the username of the Player to remove
     * @return a new Table with the new changes
     * @throws IllegalArgumentException if {@code username} is null
     */
    Table removePlayer(String username) {
        if (username == null) throw new IllegalArgumentException("username shouldn't be null");
        Player player = playerList.stream().filter(p -> p.getUsername().equals(username)).findFirst().orElse(null);
        if (player != null)
            return removePlayer(player);
        else
            return this;
    }

    /**
     * Remove the specified Player and its Board  from this Table. If no matching Player can be found, nothing is done.
     *
     * @param player the username of the Player to remove
     * @return a new Table with the new changes
     * @throws IllegalArgumentException if {@code username} is null
     */
    Table removePlayer(Player player) {
        Table t = new Table(this);
        t.playerList = new ArrayList<>(t.playerList);
        t.boardList = new ArrayList<>(t.boardList);

        boolean remove = t.playerList.remove(player);
        if (remove)
            t.boardList.removeIf(b -> b.getPlayer().equals(player));
        return t;
    }

    /**
     * Applies the given update {@link Function} to the specified {@link Player}'s {@link Board}.
     *
     * @param player the Player of whom Board to update
     * @param update the update {@link Function} to apply
     * @return a new Table with the new changes
     * @throws IllegalArgumentException if any parameter is null
     */
    Table updateBoardOf(Player player, Function<Board, Board> update) {
        if (player == null) throw new IllegalArgumentException("player shouldn't be null");
        if (update == null) throw new IllegalArgumentException("update shouldn't be null");
        Table t = new Table(this);
        t.boardList = t.boardList.stream()
                .map(b -> {
                    if (b.getPlayer().equals(player)) {
                        Board newBoard = update.apply(b);
                        if (newBoard != null)
                            return newBoard;
                    }
                    return b;
                })
                .toList();
        return t;
    }

    /**
     * Retrieves the {@link Board} of the specified {@link Player}.
     *
     * @param player the Player to whom the Board returned belongs
     * @return a Board
     */
    Board getBoardOf(Player player) {
        return boardList.stream()
                .filter(b -> b.getPlayer().equals(player))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("This player doesn't have a board associated with it"));
    }

    /**
     * Returns a copy of the list of Boards on this Table
     *
     * @return a copy of the list of Boards on this Table
     */
    List<Board> getBoards() {
        return new ArrayList<>(boardList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Table table = (Table) o;
        return sack.equals(table.sack) &&
                clouds.equals(table.clouds) &&
                motherNature.equals(table.motherNature) &&
                islandList.equals(table.islandList) &&
                professors.equals(table.professors) &&
                characters.equals(table.characters) &&
                playerList.equals(table.playerList) &&
                boardList.equals(table.boardList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(sack, clouds, motherNature, islandList, professors, characters, playerList, boardList);
    }
}
