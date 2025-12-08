package cipm.consistency.vsum.test.pcm.cprunittests.actual;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.containers.ContainersFactory;

import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.cpr.pcmjava.PcmJavaCPRUtils;
import cipm.consistency.vsum.test.pcm.cprunittests.AbstractPcmJavaCprTest;

/**
 * An abstract test class for PCM to Java CPRs that contains utility methods for
 * constructing and adding Java {@link Classifier}s into Java code models.
 * 
 * @author Alp Torac Genc
 */
public abstract class AbstractClassifierTest extends AbstractPcmJavaCprTest {
	protected static final String namespaceSeparatorRegex = "\\.";

	/**
	 * Saves and reloads the Java code model used by the VSUM. Sets the Java model
	 * stored in {@link JavaModelAccess} as the reloaded Java model.
	 */
	protected void saveAndReloadJavaModelResource() {
		JavaModelAccess.saveJavaModel();
		this.getJavaFacade().reload();
		JavaModelAccess.setJavaModel(this.getJavaFacade().getResource());
	}

	/**
	 * Creates and adds an Interface to the given Resource.
	 * 
	 * @param modelRes    Resource instance where the Interface should be inserted
	 * @param javaIfcName Interface name
	 * @param javaIfcNss  Interface namespaces
	 * @return The created Interface
	 */
	protected org.emftext.language.java.classifiers.Interface addInterfaceToJavaModelResource(Resource modelRes,
			String javaIfcName, List<String> javaIfcNss) {
		var ifc = ClassifiersFactory.eINSTANCE.createInterface();
		ifc.setName(javaIfcName);
		PcmJavaCPRUtils.addJavaClassifierIntoResource(ifc, javaIfcNss);
		return ifc;
	}

	/**
	 * Creates and adds a Class to the given Resource.
	 * 
	 * @param modelRes  Resource instance where the Class should be inserted
	 * @param className Class name
	 * @param classNss  Class namespaces
	 * @return The created Class
	 */
	protected org.emftext.language.java.classifiers.Class addClassToJavaModelResource(Resource modelRes,
			String className, List<String> classNss) {
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		cls.setName(className);
		PcmJavaCPRUtils.addJavaClassifierIntoResource(cls, classNss);
		return cls;
	}

	/**
	 * Creates and adds a Package to the given Resource.
	 * 
	 * @param modelRes Resource instance where the Package should be inserted
	 * @param pacNss   Package namespaces
	 * @return The created Package
	 */
	protected org.emftext.language.java.containers.Package addPackageToJavaModelResource(Resource modelRes,
			List<String> pacNss) {
		var pac = ContainersFactory.eINSTANCE.createPackage();
		pac.getNamespaces().addAll(pacNss);
		modelRes.getContents().add(pac);
		return pac;
	}

	/**
	 * Creates and adds a Module to the given Resource.
	 * 
	 * @param modelRes Resource instance where the Package should be inserted
	 * @param modNss   Module namespaces
	 * @return The created Module
	 */
	protected org.emftext.language.java.containers.Module addModuleToJavaModelResource(Resource modelRes,
			List<String> modNss) {
		var mod = ContainersFactory.eINSTANCE.createModule();
		mod.getNamespaces().addAll(modNss);

		// Remove the superfluous dot in mod.getNamespacesAsString()
		mod.setName(mod.getNamespacesAsString().substring(0, mod.getNamespacesAsString().length() - 1));
		modelRes.getContents().add(mod);
		return mod;
	}
}
