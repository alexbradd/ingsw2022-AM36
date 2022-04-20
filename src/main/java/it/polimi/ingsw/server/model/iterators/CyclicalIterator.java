package it.polimi.ingsw.server.model.iterators;

import java.util.Iterator;
import java.util.List;

// STUB
public class CyclicalIterator<T> implements Iterator<T> {
    private final List<T> list;
    private int currentIndex;

    public CyclicalIterator(List<T> list) {
        if (list == null) throw new IllegalArgumentException("list cannot be null");
        this.list = list;
        currentIndex = 0;
    }

    public CyclicalIterator(List<T> list, int startingIndex) {
        if (list == null) throw new IllegalArgumentException("list cannot be null");
        if (startingIndex < 0 || startingIndex > list.size())
            throw new IllegalArgumentException("startingIndex is invalid");
        this.list = list;
        currentIndex = startingIndex + 1;
    }

    public CyclicalIterator(List<T> list, T start) {
        if (list == null) throw new IllegalArgumentException("list shouldn't be null");
        if (start == null) throw new IllegalArgumentException("start shouldn't be null");
        if (!list.contains(start))
            throw new IllegalArgumentException("cannot start from an island that is not in the list");
        this.list = list;
        currentIndex = list.indexOf(start) + 1;
    }

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public T next() {
        if (currentIndex >= list.size())
            currentIndex = 0;
        T current = list.get(currentIndex);
        currentIndex++;
        if (currentIndex >= list.size())
            currentIndex = 0;
        return current;
    }
}
