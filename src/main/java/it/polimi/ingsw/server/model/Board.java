package it.polimi.ingsw.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Represents a snapshot of the game's playing board, including players. This is essentially a data holder manipulated
 * by the state machine.
 *
 * @author Alexandru Gabriel Bradatan
 */
class Board {
    /**
     * The {@link Sack} instance.
     */
    private Sack sack;

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
     * A List<{@link Player}> instance representing the connected players.
     */
    private List<Player> players;

    /**
     * Creates a new empty board. All members are uninitialized and need to be created.
     */
    Board() {
    }

    /**
     * Creates a new Board that is the shallow copy of the given one.
     *
     * @param old the Board to copy
     * @throws IllegalArgumentException if {@code old} is null
     */
    Board(Board old) {
        if (old == null) throw new IllegalArgumentException("old cannot be null");
        this.sack = old.sack;
        this.clouds = old.clouds;
        this.motherNature = old.motherNature;
        this.islandList = old.islandList;
        this.professors = old.professors;
        this.characters = old.characters;
        this.players = old.players;
    }

    /**
     * Getter for the Sack stored.
     *
     * @return the Sack stored
     */
    Sack getSack() {
        return sack;
    }

    /**
     * Applies the given update to the Sack stored.
     *
     * @param update the update to apply
     * @return a new Board with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Board updateSack(Function<Sack, Sack> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Board r = new Board(this);
        r.sack = update.apply(r.sack);
        return r;
    }

    /**
     * Getter for the list of Cloud stored.
     *
     * @return a duplicate of the list of Cloud stored
     */
    List<Cloud> getClouds() {
        return duplicateIfNotNull(clouds);
    }

    /**
     * Private helper that duplicates the given list if not null. If the list is null, return null
     *
     * @param list the list to duplicate
     * @param <T>  generic type
     * @return the duplicate of the list or null
     */
    private <T> List<T> duplicateIfNotNull(List<T> list) {
        return list == null ? null : new ArrayList<>(list);
    }

    /**
     * Applies the given update to the list of Cloud stored.
     *
     * @param update the update to apply
     * @return a new Board with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Board updateClouds(Function<List<Cloud>, List<Cloud>> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Board r = new Board(this);
        r.clouds = applyToDuplicate(r.clouds, update);
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
        return update.apply(duplicateIfNotNull(list));
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
     * @return a new Board with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Board updateMotherNature(Function<MotherNature, MotherNature> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Board r = new Board(this);
        r.motherNature = update.apply(r.motherNature);
        return r;
    }

    /**
     * Getter for the list of Island stored.
     *
     * @return a duplicate of the list of Island stored
     */
    List<Island> getIslandList() {
        return duplicateIfNotNull(islandList);
    }

    /**
     * Applies the given update to the list of Island stored.
     *
     * @param update the update to apply
     * @return a new Board with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Board updateIslandList(Function<List<Island>, List<Island>> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Board r = new Board(this);
        r.islandList = applyToDuplicate(r.islandList, update);
        return r;
    }

    /**
     * Getter for the list of Professor stored.
     *
     * @return a duplicate of the list of Professor stored
     */
    List<Professor> getProfessors() {
        return duplicateIfNotNull(professors);
    }

    /**
     * Applies the given update to the list of Professor stored.
     *
     * @param update the update to apply
     * @return a new Board with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Board updateProfessors(Function<List<Professor>, List<Professor>> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Board r = new Board(this);
        r.professors = applyToDuplicate(r.professors, update);
        return r;
    }

    /**
     * Getter for the list of Character stored.
     *
     * @return a duplicate of the list of Character stored
     */
    List<Character> getCharacters() {
        return duplicateIfNotNull(characters);
    }

    /**
     * Applies the given update to the list of Character stored.
     *
     * @param update the update to apply
     * @return a new Board with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Board updateCharacters(Function<List<Character>, List<Character>> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Board r = new Board(this);
        r.characters = applyToDuplicate(r.characters, update);
        return r;
    }

    /**
     * Getter for the list of Player stored.
     *
     * @return a duplicate of the list of Player stored
     */
    List<Player> getPlayers() {
        return duplicateIfNotNull(players);
    }

    /**
     * Applies the given update to the list of Player stored. Note: each update to the players causes a reassignment
     * of the Professors.
     *
     * @param update the update to apply
     * @return a new Board with the update applied
     * @throws IllegalArgumentException if {@code update} is null
     */
    Board updatePlayers(Function<List<Player>, List<Player>> update) {
        if (update == null) throw new IllegalArgumentException("update cannot be null");
        Board r = new Board(this);
        r.players = applyToDuplicate(r.players, update);
        r.professors = new ArrayList<>(r.professors);
        r.professors.replaceAll(professor -> {
            if (professor.getOwner().isEmpty())
                return professor;
            Player oldOwner = professor.getOwner().get();
            Player newOwner = r.players.stream()
                    .filter(p -> Objects.equals(oldOwner.getUsername(), p.getUsername()))
                    .findAny()
                    .orElseThrow(IllegalStateException::new);
            return new Professor(professor.getColor(), newOwner);
        });
        return r;
    }
}
