package it.polimi.ingsw.server.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents the game's players. It is a mutable list of {@link Player} with a maximum allowed size.
 * The list offers two additional, custom ways of iterating.
 * For more information about the iterators, see {@link ClockWiseIterator} and {@link AssistantValueIterator}.
 *
 * @author Mattia Busso
 * @see Player
 */
class PlayerList extends AbstractList<Player> {

    /**
     * Internal {@link Player} store.
     */
    final private List<Player> list;

    /**
     * The maximum allowed size of the list.
     */
    final private int maxSize;

    /**
     * Basic constructor.
     *
     * @param max maximum size of the list
     * @throws IllegalArgumentException if max <= 0 or max > 4 (a game with more than four players is not allowed)
     */
    PlayerList(int max) throws IllegalArgumentException {
        if(max <= 0 || max > 4) {
            throw new IllegalArgumentException("given list max size " + max + " is invalid");
        }
        maxSize = max;
        list = new ArrayList<>();
    }

    // Iterators

    /**
     * Returns a new {@link ClockWiseIterator} instance.
     *
     * @param startIndex the starting index from which to iterate on the list
     * @return a {@link ClockWiseIterator} implementation of a {@code PlayerListIterator}
     */
    ClockWiseIterator clockWiseIterator(int startIndex) {
        return new ClockWiseIterator(this, startIndex);
    }

    /**
     * Returns a new {@link AssistantValueIterator} instance.
     *
     * @param startIndex the index of the last player to play first
     * @return a {@link AssistantValueIterator} implementation of a {@code PlayerListIterator}
     */
    AssistantValueIterator assistantValueIterator(int startIndex) {
        return new AssistantValueIterator(this, startIndex);
    }

    // List access methods

    /**
     * Returns {@code true} if the given {@link Player} is inside the list.
     *
     * @param player the {@code Player} to check if contained inside the list
     * @return {@code true} if {@code Player} is inside the list, {@code false} otherwise
     * @throws IllegalArgumentException if {@code player == null}
     */
    boolean contains(Player player) throws IllegalArgumentException {
        if(player == null) {
            throw new IllegalArgumentException("player shouldn't be null");
        }
        try {
            get(player.getUsername());
            return true;
        }
        catch(IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Returns the {@link Player} at the given position.
     *
     * @param index position of the player to return
     * @return the player at the given position
     * @throws IndexOutOfBoundsException if index is out of range
     */
    @Override
    public Player get(int index) throws IndexOutOfBoundsException {
        return list.get(index);
    }

    /**
     * Returns the {@link Player} with the given username.
     *
     * @param username username of the player to return
     * @return the player with the given username
     * @throws IllegalArgumentException if the player with the given username is not in the list
     */
    Player get(String username) throws IllegalArgumentException {
        for (int i = 0; i < size(); i++) {
            if (Objects.equals(get(i).getUsername(), username)) return list.get(i);
        }
        throw new IllegalArgumentException("can't find Player with username: \"" + username + "\" in the list");
    }

    /**
     * Returns the size of this list.
     *
     * @return the size of the list
     */
    @Override
    public int size() {
        return list.size();
    }

    /**
     * Appends the given {@link Player} to the end of the list.
     *
     * @param p the player to be appended to the list
     * @return true (as specified by {@code Collection.add(E)}
     * @throws IllegalStateException if {@code size() == maxSize}
     * @throws IllegalArgumentException if there's already a {@code Player} in the list with the same username as {@code Player p} or {@code p == null}
     */
    @Override
    public boolean add(Player p) throws IllegalStateException, IllegalArgumentException {
        if(p == null) {
            throw new IllegalArgumentException("p shouldn't be null");
        }
        if(size() == maxSize) {
            throw new IllegalStateException("the list is full");
        }
        if(contains(p)) {
            throw new IllegalArgumentException("can't have two players with the same username in the list");
        }
        return list.add(p);
    }

    /**
     * Returns true if this list contains no elements.
     *
     * @return {@code true} if this list contains no elements
     */
    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Removes the {@link Player} at the specified position in this list.
     *
     * @param index the index of the {@code Player} to be removed
     * @return the {@code Player} previously at the specified location
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public Player remove(int index) throws IndexOutOfBoundsException {
        return list.remove(index);
    }

    /**
     * Removes the {@link Player} with the specified username in this list.
     *
     * @param username the username of the {@code Player} to be removed
     * @return the {@code Player} previously with the specified username
     * @throws IllegalArgumentException if a {@code Player} with the given username is not present in this list
     */
    Player remove(String username) throws IllegalArgumentException {
        for(int i = 0; i < size(); i++) {
            if(Objects.equals(get(i).getUsername(), username)) return remove(i);
        }
        throw new IllegalArgumentException("can't find Player with username: \"" + username + "\" in the list");
    }

    @Override
    public Player set(int index, Player element) {
        return list.set(index, element);
    }
}
