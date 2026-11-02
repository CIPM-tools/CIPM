package cipm.consistency.performance.tests.testutil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import tools.vitruv.change.propagation.ResourceAccess;

/**
 * A {@link ResourceAccess} stub whose every method throws. Used where a change is expected to
 * match no reaction/transformation rule at all - on that path, none of these methods are ever
 * actually called, so a throwing stub both satisfies the parameter and proves it unused.
 */
public final class UnsupportedResourceAccess implements ResourceAccess {

	@Override
	public URI getMetadataModelURI(String... metadataKey) {
		throw new UnsupportedOperationException("not expected to be called on the no-match path");
	}

	@Override
	public Resource getModelResource(URI uri) {
		throw new UnsupportedOperationException("not expected to be called on the no-match path");
	}

	@Override
	public void persistAsRoot(EObject rootObject, URI uri) {
		throw new UnsupportedOperationException("not expected to be called on the no-match path");
	}
}
