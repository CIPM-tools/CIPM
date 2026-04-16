package cipm.consistency.commitintegration.diff.util;

import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.scope.DefaultComparisonScope;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * The scope for an EMF Compare comparison which filters resources.
 * 
 * @author Martin Armbruster
 */
public class ResourceListFilteringComparisonScope extends DefaultComparisonScope {
    /**
     * Creates a new instance.
     * 
     * @param left
     *            the left or new model.
     * @param right
     *            the right or old model.
     * @param newResources
     *            a list of new model Resources. Can be null. If the list is not null, only
     *            Resources within the list are compared.
     * @param currentResources
     *            a list of old model Resources. Can be null. If the list is not null, only
     *            Resources within the list are compared.
     */
    public ResourceListFilteringComparisonScope(Notifier left, Notifier right, List<Resource> newResources,
            List<Resource> currentResources) {
        super(left, right, null);
        this.setResourceSetContentFilter(r -> {
            if (newResources != null && currentResources != null) {
                return r.getResourceSet() == left && newResources.contains(r)
                        || r.getResourceSet() == right && currentResources.contains(r);
            }
            return true;
        });
    }
}
