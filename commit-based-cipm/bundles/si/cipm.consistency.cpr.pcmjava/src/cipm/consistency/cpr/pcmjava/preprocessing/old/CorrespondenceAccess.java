package cipm.consistency.cpr.pcmjava.preprocessing.old;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;

import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.Correspondences;

public class CorrespondenceAccess {
	private static Resource corModel;

	private CorrespondenceAccess() {
	}

	public static Set<Correspondence> getAllCorrespondences() {
		if (corModel == null || !corModel.isLoaded())
			return null;
		if (corModel.getContents().isEmpty() || !(corModel.getContents().get(0) instanceof Correspondences))
			return null;

		return new HashSet<>(((Correspondences) corModel.getContents().get(0)).getCorrespondences());
	}

	public static Set<Correspondence> getAllCorrespondencesFor(EObject obj) {
		return getAllCorrespondences().stream()
				.filter((c) -> c.getLeftEObjects().stream().anyMatch((lhsO) -> EcoreUtil.equals(lhsO, obj))
						|| c.getRightEObjects().stream().anyMatch((rhsO) -> EcoreUtil.equals(rhsO, obj)))
				.collect(Collectors.toCollection(HashSet::new));
	}

	public static Set<Correspondence> getAllCorrespondencesFor(EObject obj1, EObject obj2) {
		return getAllCorrespondences().stream()
				.filter((c) -> c.getLeftEObjects().stream().anyMatch((lhsO) -> EcoreUtil.equals(lhsO, obj1))
						&& c.getRightEObjects().stream().anyMatch((rhsO) -> EcoreUtil.equals(rhsO, obj2))
						|| c.getLeftEObjects().stream().anyMatch((lhsO) -> EcoreUtil.equals(lhsO, obj2))
								&& c.getRightEObjects().stream().anyMatch((rhsO) -> EcoreUtil.equals(rhsO, obj1)))
				.collect(Collectors.toCollection(HashSet::new));
	}

	public static Correspondence getCorrespondenceFor(EObject obj1, EObject obj2, String tag) {
		return getAllCorrespondences().stream()
				.filter((c) -> (c.getLeftEObjects().stream().anyMatch((lhsO) -> EcoreUtil.equals(lhsO, obj1))
						&& c.getRightEObjects().stream().anyMatch((rhsO) -> EcoreUtil.equals(rhsO, obj2))
						|| c.getLeftEObjects().stream().anyMatch((lhsO) -> EcoreUtil.equals(lhsO, obj2))
								&& c.getRightEObjects().stream().anyMatch((rhsO) -> EcoreUtil.equals(rhsO, obj1))
								&& c.getTag().equals(tag)))
				.findFirst().orElseGet(() -> null);
	}

	public static Set<Correspondence> getAllCorrespondencesFor(String tag) {
		return getAllCorrespondences().stream().filter((c) -> c.getTag().equals(tag))
				.collect(Collectors.toCollection(HashSet::new));
	}

	/**
	 * Unloads the current Java code model, if it is loaded.
	 */
	public static void unloadCorModel() {
		if (corModel != null && corModel.isLoaded()) {
			corModel.unload();
		}
	}

	/**
	 * Loads the Java code model at the given URI.
	 */
	public static void loadCorModel(URI corModelResourceFileURI) {
		// Unload potential previous Java code model
		unloadCorModel();

		var resSet = new ResourceSetImpl();
		corModel = resSet.createResource(corModelResourceFileURI);

		try {
			corModel.load(null);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	public static void setCorModel(Resource corModelResource) {
		corModel = corModelResource;
		if (corModel != null && !corModel.isLoaded()) {
			try {
				corModel.load(null);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
		}
	}

	/**
	 * @return The currently loaded Java model resource
	 */
	public static Resource getCorModel() {
		return corModel;
	}
}
