package cipm.consistency.cpr.pcmjava.preprocessing;

import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.atomic.eobject.EObjectAddedEChange;
import tools.vitruv.change.atomic.eobject.EObjectExistenceEChange;
import tools.vitruv.change.atomic.eobject.EObjectSubtractedEChange;
import tools.vitruv.change.atomic.feature.FeatureEChange;
import tools.vitruv.change.atomic.root.RootEChange;

/**
 * A utility class that provides various methods regarding {@link EChange}
 * instances. These methods account for the given EChange instances being
 * incompatible and do not throw exceptions for missing
 * {@link EStructuralFeature}s of the EChanges.
 * 
 * @author Alp Torac Genc
 */
public final class ChangeUtil {
	private static final String cacheIDPrefix = "cache:/";

	/**
	 * @return Whether the given URI is a cache URI (i.e. whether it starts with
	 *         {@value #cacheIDPrefix})
	 */
	public static boolean isCacheURI(String uri) {
		return uri.startsWith(cacheIDPrefix);
	}

	/**
	 * Modifies the URIs present in the model changes as EChanges (from
	 * changeResource) to be compatible with targetModelResource. Skips the given
	 * URI prefixes while doing so.
	 */
	public static void adaptChangeURIs(Resource changeResource, Resource targetModelResource,
			List<String> uriPrefixesToSkip) {
		for (var change : changeResource.getContents()) {
			if (change instanceof EChange)
				adaptChangeURIs((EChange) change, targetModelResource, uriPrefixesToSkip);
		}
	}

	/**
	 * A variant of {@link #adaptChangeURIs(Resource, Resource, List)} without any
	 * ignored URI prefixes.
	 */
	public static void adaptChangeURIs(Resource changeResource, Resource targetModelResource) {
		adaptChangeURIs(changeResource, targetModelResource, List.of());
	}

	/**
	 * @return Whether the given uri starts with any of the given prefixes.
	 */
	public static boolean uriStartsWith(String uri, List<String> prefixes) {
		return prefixes.stream().anyMatch((p) -> uriStartsWith(uri, p));
	}

	/**
	 * @return Whether the given uri starts with the given prefix.
	 */
	public static boolean uriStartsWith(String uri, String prefix) {
		return uri.startsWith(prefix);
	}

	/**
	 * Modifies the URIs present in the given change to be compatible with
	 * targetModelResource. Skips the given URI prefixes while doing so.
	 */
	public static void adaptChangeURIs(EChange change, Resource targetModelResource, List<String> uriPrefixesToSkip) {
		var affectedID = getAffectedEObjectID(change);
		if (affectedID != null && !uriStartsWith(affectedID, uriPrefixesToSkip)) {
			setAffectedEObjectID(change, rebaseURI(affectedID, targetModelResource));
		}
		var oldID = getOldValueID(change);
		if (oldID != null && !uriStartsWith(oldID, uriPrefixesToSkip)) {
			setOldValueID(change, rebaseURI(oldID, targetModelResource));
		}
		var newID = getNewValueID(change);
		if (newID != null && !uriStartsWith(newID, uriPrefixesToSkip)) {
			setNewValueID(change, rebaseURI(newID, targetModelResource));
		}
		var uri = getRootChangeURI(change);
		if (uri != null && !uriStartsWith(uri, uriPrefixesToSkip)) {
			setRootChangeURI(change, targetModelResource.getURI().toString());
		}
	}

	/**
	 * Ignores cache URIs (URIs that start with {@value #cacheIDPrefix})
	 * 
	 * @param uri A given URI as String, whose fragment will be re-based onto the
	 *            URI of the given res
	 * @param res A given resource, whose URI should be the new basis for the given
	 *            uri
	 * @return {@code res.getURI() + uri.fragment}. If uri is a cache URI, returns
	 *         the given uri.
	 */
	private static String rebaseURI(String uri, Resource res) {
		if (isCacheURI(uri))
			return uri;

		var fragment = URI.createURI(uri).fragment();
		return res.getURI().appendFragment(fragment).toString();
	}

	public static String getRootChangeURI(EChange change) {
		if (change instanceof RootEChange) {
			return ((RootEChange) change).getUri();
		}
		return null;
	}

	public static void setRootChangeURI(EChange change, String newURI) {
		if (change instanceof RootEChange) {
			((RootEChange) change).setUri(newURI);
		}
	}

	public static String getAffectedEObjectID(EChange change) {
		if (change instanceof EObjectExistenceEChange)
			return ((EObjectExistenceEChange<?>) change).getAffectedEObjectID();
		if (change instanceof FeatureEChange)
			return ((FeatureEChange<?, ?>) change).getAffectedEObjectID();
		return null;
	}

	public static void setAffectedEObjectID(EChange change, String newID) {
		if (change instanceof EObjectExistenceEChange)
			((EObjectExistenceEChange<?>) change).setAffectedEObjectID(newID);
		if (change instanceof FeatureEChange)
			((FeatureEChange<?, ?>) change).setAffectedEObjectID(newID);
	}

	public static String getNewValueID(EChange change) {
		if (change instanceof EObjectAddedEChange)
			return ((EObjectAddedEChange<?>) change).getNewValueID();
		return null;
	}

	public static String getOldValueID(EChange change) {
		if (change instanceof EObjectSubtractedEChange)
			return ((EObjectSubtractedEChange<?>) change).getOldValueID();
		return null;
	}

	public static void setNewValueID(EChange change, String newID) {
		if (change instanceof EObjectAddedEChange)
			((EObjectAddedEChange<?>) change).setNewValueID(newID);
	}

	public static void setOldValueID(EChange change, String newID) {
		if (change instanceof EObjectSubtractedEChange)
			((EObjectSubtractedEChange<?>) change).setOldValueID(newID);
	}
}
