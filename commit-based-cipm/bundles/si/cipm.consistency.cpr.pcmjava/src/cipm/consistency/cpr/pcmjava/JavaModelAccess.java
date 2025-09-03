package cipm.consistency.cpr.pcmjava;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Predicate;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

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
	public static Collection<EObject> getAllTopLevelJavaModelElements() {
		// TODO Implement
		return null;
	}

	/**
	 * @return All top-level (i.e. not contained in any parent EObject) EObjects
	 *         from the Java code model satisfying the given filter, whose
	 *         architecture is modeled in its PCM counterpart.
	 */
	public static Collection<EObject> getAllTopLevelJavaModelElements(Predicate<EObject> filter) {
		// TODO Implement
		return null;
	}

	/**
	 * Unloads the current Java code model, if it is loaded.
	 */
	public static void unloadJavaModel() {
		if (javaModel != null && javaModel.isLoaded()) {
			javaModel.unload();
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
	}

	/**
	 * @return The currently loaded Java model resource
	 */
	public static Resource getJavaModel() {
		return javaModel;
	}
}
