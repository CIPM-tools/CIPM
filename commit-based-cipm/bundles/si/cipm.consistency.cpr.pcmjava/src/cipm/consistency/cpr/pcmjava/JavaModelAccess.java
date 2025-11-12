package cipm.consistency.cpr.pcmjava;

import java.io.IOException;
import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;

import com.google.common.base.Strings;

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

	private static EObject getSyntheticCompilationUnit() {
		if (javaModel == null)
			return null;
		var cu = javaModel.getContents().stream().filter((o) -> o instanceof CompilationUnit)
				.filter((o) -> Strings.isNullOrEmpty(((CompilationUnit) o).getName())).findFirst();
		return cu.orElseGet(() -> null);
	}

	/**
	 * Ignores synthetic elements ({@link jamopp.recovery.trivial.TrivialRecovery})
	 */
	public static List<EObject> getTopLevelJavaModelElements() {
		var topLevelContents = new ArrayList<>(javaModel.getContents());
		topLevelContents.remove(getSyntheticCompilationUnit());
		return topLevelContents;
	}

	public static boolean isInJavaModelResource(EObject obj) {
		return obj.eResource() != null && obj.eResource() == javaModel;
	}

	/**
	 * Ignores synthetic elements ({@link jamopp.recovery.trivial.TrivialRecovery}).
	 * ConcreteClassifier have to be contained in a CompilationUnit to be eligible
	 * here.
	 * 
	 * @return A set of ConcreteClassifier found in the Java code model, whose name
	 *         (without namespaces) matches the given name.
	 */
	public static Set<ConcreteClassifier> findPotentialConcreteClassifiers(String name) {
		var clsSet = new HashSet<ConcreteClassifier>();
		var synthethicCU = getSyntheticCompilationUnit();
		javaModel.getAllContents().forEachRemaining((o) -> {
			if (o instanceof ConcreteClassifier) {
				var castedO = ((ConcreteClassifier) o);
				if (castedO.getContainingCompilationUnit() != synthethicCU) {
					clsSet.add(castedO);
				}
			}
		});
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
