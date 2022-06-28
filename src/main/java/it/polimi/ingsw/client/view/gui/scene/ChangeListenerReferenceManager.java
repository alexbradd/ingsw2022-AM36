package it.polimi.ingsw.client.view.gui.scene;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Keeps track of observables and their listeners for easy disposal
 */
public class ChangeListenerReferenceManager {
    /**
     * A map that links an {@link ObservableValue} with its listener removers
     */
    private final HashMap<ObservableValue<?>, List<Runnable>> changeListeners;

    /**
     * Creates a new instance of the class
     */
    public ChangeListenerReferenceManager() {
        changeListeners = new HashMap<>();
    }

    /**
     * Registers a new listener for the given {@link ObservableValue} and adds it to the value.
     *
     * @param value    the {@link ObservableValue}
     * @param listener the {@link ChangeListener}
     * @param <T>      the type of the value
     * @throws IllegalArgumentException if any parameter is null
     */
    public <T> void registerListener(ObservableValue<T> value, ChangeListener<T> listener) {
        if (value == null) throw new IllegalArgumentException("value shouldn't be null");
        if (listener == null) throw new IllegalArgumentException("listener shouldn't be null");
        if (!changeListeners.containsKey(value))
            changeListeners.put(value, new ArrayList<>());
        value.addListener(listener);
        changeListeners.get(value).add(() -> value.removeListener(listener));
    }

    /**
     * Unregisters and removes all known listeners.
     */
    public void unregisterAll() {
        changeListeners.keySet().forEach(this::unregisterFor);
    }

    /**
     * Unregisters and removes all listeners for the given value, if known.
     *
     * @param value the {@link ObservableValue}
     * @throws IllegalArgumentException if {@code value} is null
     */
    public void unregisterFor(ObservableValue<?> value) {
        if (value == null) throw new IllegalArgumentException("value shouldn't be null");
        List<Runnable> removers = changeListeners.get(value);
        if (removers != null)
            removers.forEach(Runnable::run);
    }
}
