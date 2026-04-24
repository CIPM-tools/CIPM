package cipm.consistency.base.shared.util;

/**
 * Used together with {@link AbstractObservable} to create observable entities.
 * 
 * @author David Monschein
 *
 * @param <T>
 *            type of the message that is used to publish changes
 */
@FunctionalInterface
public interface IGenericListener<T> {

    /**
     * Informs the listener about a change.
     * 
     * @param data
     *            the information about the change
     */
    public void inform(T data);

}
