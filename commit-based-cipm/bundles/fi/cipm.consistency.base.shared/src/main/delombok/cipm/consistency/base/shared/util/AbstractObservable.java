package cipm.consistency.base.shared.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class that provides the functionality to make an entity observable.
 * 
 * @author David Monschein
 *
 * @param <T>
 *            type that is used to publish change information
 */
public abstract class AbstractObservable<T> {

    private List<IGenericListener<T>> listeners;

    /**
     * Creates a new observable entity with no listeners.
     */
    public AbstractObservable() {
        this.listeners = new ArrayList<>();
    }

    /**
     * Gets all listeners of the entity.
     * 
     * @return list of all listeners
     */
    public List<IGenericListener<T>> getListeners() {
        return listeners;
    }

    /**
     * Adds a listener to the entity.
     * 
     * @param listener
     *            the listener to add
     */
    public void addListener(IGenericListener<T> listener) {
        this.listeners.add(listener);
    }

    /**
     * Publishes the given information to all listeners.
     * 
     * @param data
     *            information that should be published
     */
    protected void flood(T data) {
        listeners.forEach(l -> {
            l.inform(data);
        });
    }

}
