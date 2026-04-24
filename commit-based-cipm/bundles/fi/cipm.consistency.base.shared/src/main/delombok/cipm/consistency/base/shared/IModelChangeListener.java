package cipm.consistency.base.shared;

import org.eclipse.emf.common.notify.Notification;

/**
 * Simple interface which describes a listener that is notified when a model changes.
 * 
 * @author David Monschein
 *
 */
@FunctionalInterface
public interface IModelChangeListener {

    /**
     * Called when a model changes.
     * 
     * @param notification
     *            the change notification
     */
    public void onChange(Notification notification);

}
