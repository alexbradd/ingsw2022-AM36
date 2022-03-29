package it.polimi.ingsw.server.model;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Random;

/**
 * Represents the game's islands. It is a fixed-length, immutable list of {@link Island}. Each change in island control
 * triggers a {@code scrub()}, an operation that iterates through the list and creates every group it can. The list can
 * be iterated in the same fashion as every other list, or it can be iterated in cyclical fashion skipping groups. For
 * more information on this iterator, see {@link IslandListIterator}.
 *
 * @author Alexandru Gabriel Bradatan
 * @see Island
 */
class IslandList extends AbstractList<Island> {
    /**
     * Internal {@link Island} store.
     */
    private final Island[] pieces;

    /**
     * Base constructor
     */
    IslandList() {
        pieces = new Island[12];
        for (int i = 0; i < 12; i++)
            pieces[i] = new Island(i, this);
    }

    /**
     * Returns the size of this list.
     *
     * @return the size of the list
     */
    @Override
    public int size() {
        return pieces.length;
    }

    /**
     * Returns the {@link Island} at the given position.
     *
     * @param index position of the island to return
     * @return the island at the given position
     * @throws IndexOutOfBoundsException if index is out of range
     */
    @Override
    public Island get(int index) {
        if (index < 0 || index > 12) throw new IndexOutOfBoundsException("given index " + index + "is out of range");
        return pieces[index];
    }

    /**
     * Returns the number of groups in this list.
     *
     * @return the number of groups in this list
     */
    int getNumOfGroups() {
        int tot = 0;
        Island currentGroup = null;
        for (Island piece : pieces) {
            if (currentGroup == null || !piece.isRelatedTo(currentGroup)) {
                tot++;
                currentGroup = piece;
            }
        }
        return tot;
    }

    /**
     * Scrubs the list merging {@link Island} where it can.
     */
    void scrub() {
        for (int i = 0; i < pieces.length; i++) {
            Island p1 = pieces[i];
            Island p2 = i >= pieces.length - 1 ? pieces[0] : pieces[i + 1];
            if (p1.canBeMergedWith(p2)) p1.merge(p2);
        }
    }

    /**
     * Returns an iterator over the groups in this list in cyclical fashion starting from the first island.
     *
     * @return an iterator over the elements in this list in cyclical fashion
     */
    public IslandListIterator groupIterator() {
        return new IslandListIterator(this);
    }

    /**
     * Returns an iterator over the groups in this list in cyclical fashion starting from a random position.
     *
     * @return an iterator over the elements in this list in cyclical fashion
     */
    public IslandListIterator randomGroupIterator() {
        Random r = new Random();
        return new IslandListIterator(this, r.nextInt(pieces.length));
    }

    /**
     * Returns a string representation of this island list.
     *
     * @return a string representation of this island list
     */
    @Override
    public String toString() {
        return "IslandList{" + "pieces=" + Arrays.toString(pieces) + '}';
    }
}
