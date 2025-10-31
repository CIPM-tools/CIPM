package cipm.consistency.cpr.pcmjava;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.emftext.language.java.classifiers.ConcreteClassifier;

/**
 * A utility class that grants access to all Java code model elements. <br>
 * <br>
 * It is needed in the CPRs, because there is no root object defined in the
 * JaMoPP metamodel, which contains all EObjects of the Java code model. Since
 * there is no obligation for all Java code model elements to have
 * correspondences to PCM model elements, this class is a necessary workaround.
 * 
 * @author Alp Torac Genc
 */
public final class JavaModelAccess {
	private static Resource javaModel;

	private JavaModelAccess() {
	}

	/**
	 * @return All top-level (i.e. not contained in any parent EObject) EObjects
	 *         from the Java code model, whose architecture is modeled in its PCM
	 *         counterpart.
	 */
	public static Collection<EObject> getTopLevelJavaModelElements() {
		return getTopLevelJavaModelElements(null);
	}

	/**
	 * @return All top-level (i.e. not contained in any parent EObject) EObjects
	 *         from the Java code model satisfying the given filter, whose
	 *         architecture is modeled in its PCM counterpart.
	 */
	public static Collection<EObject> getTopLevelJavaModelElements(Predicate<EObject> filter) {
		if (filter == null)
			return new ArrayList<>(javaModel.getContents());

		return javaModel.getContents().stream().filter(filter).collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 * @return All contents inside the Java code model:
	 *         {@code javaModel.getAllContents()}
	 */
	public static Collection<EObject> getAllJavaModelElements() {
		var contents = new ArrayList<EObject>();
		javaModel.getAllContents().forEachRemaining(contents::add);
		return contents;
	}

	/**
	 * @return A set of Classifiers found in the Java code model, whose name
	 *         (without namespaces) matches the given name.
	 */
	public static Set<ConcreteClassifier> findPotentialConcreteClassifiers(String name) {
		var clsSet = new HashSet<ConcreteClassifier>();

		getAllJavaModelElements().stream().filter((o) -> o instanceof ConcreteClassifier)
				.map((cls) -> ((ConcreteClassifier) cls))
				.filter((cls) -> cls.getName() != null && cls.getName().equals(name)).forEach(clsSet::add);

		return clsSet;
	}

	/**
	 * Unloads the current Java code model, if it is loaded.
	 */
	public static void unloadJavaModel() {
		if (javaModel != null && javaModel.isLoaded()) {
			javaModel.unload();
		}
	}

	public static void removeJavaModel() {
		unloadJavaModel();
		if (javaModel != null) {
			var resSet = javaModel.getResourceSet();
			if (resSet != null) {
				resSet.getResources().clear();
			}
			javaModel = null;
		}
	}

	/**
	 * Loads the Java code model at the given URI.
	 */
	public static void loadJavaModel(URI javaModelResourceFileURI) {
		// Unload potential previous Java code model
		unloadJavaModel();

		var resSet = new ResourceSetImpl();
		javaModel = resSet.createResource(javaModelResourceFileURI);

		try {
			javaModel.load(null);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

	public static void setJavaModel(Resource javaModelResource) {
		javaModel = javaModelResource;
		if (javaModel != null && !javaModel.isLoaded()) {
			try {
				javaModel.load(null);
			} catch (IOException e) {
				e.printStackTrace();
				throw new IllegalArgumentException(e);
			}
		}
	}

	public static void saveJavaModel() {
		try {
			javaModel.save(null);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IllegalStateException(e);
		}
	}

	/**
	 * @return The currently loaded Java model resource
	 */
	public static Resource getJavaModel() {
		return javaModel;
	}
}
