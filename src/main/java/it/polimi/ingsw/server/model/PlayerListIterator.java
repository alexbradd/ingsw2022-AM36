package it.polimi.ingsw.server.model;

import java.util.Iterator;

/**
 * This class provides a skeletal setup implementation of the {@link Iterator} interface
 * to the use of concrete {@link PlayerList} iterators (such as {@link ClockWiseIterator} and {@link AssistantValueIterator}).
 * The class offers a common constructor for the {@code PlayerList list} attribute, which is shared between concrete iterators.
 *
 * @author Mattia Busso
 * @see PlayerList
 * @see ClockWiseIterator
 * @see AssistantValueIterator
 */
abstract class PlayerListIterator implements Iterator<Player> {

    /**
     * List that the object iterates on.
     */
    private final PlayerList list;

    /**
     * Basic constructor.
     *
     * @param list the list to iterate on
     * @throws IllegalArgumentException if {@code list} is {@code null}
     */
    PlayerListIterator(PlayerList list) throws IllegalArgumentException {
        if(list == null) {
            throw new IllegalArgumentException("list shouldn't be null");
        }
        this.list = list;
    }

    /**
     * The {@code list} attribute getter.
     *
     * @return the list to iterate on
     */
    PlayerList getList() {
        return list;
    }

}
