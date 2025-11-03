package cipm.consistency.vsum.test.pcm.cprunittests.actual;

import java.util.List;

import org.eclipse.emf.ecore.resource.Resource;
import org.emftext.language.java.classifiers.ClassifiersFactory;
import org.emftext.language.java.containers.ContainersFactory;

import cipm.consistency.cpr.pcmjava.JavaModelAccess;
import cipm.consistency.cpr.pcmjava.PcmJavaCPRUtils;
import cipm.consistency.vsum.test.pcm.cprunittests.AbstractPcmJavaCprTest;

public abstract class AbstractClassifierTest extends AbstractPcmJavaCprTest {
	protected static final String namespaceSeparatorRegex = "\\.";

	protected boolean namespacesEqual(List<String> nss1, List<String> nss2) {
		if (nss1.size() != nss2.size())
			return false;

		for (int i = 0; i < nss1.size(); i++) {
			if (!nss1.get(i).equals(nss2.get(i)))
				return false;
		}

		return true;
	}

	protected void saveAndReloadJavaModelResource() {
		JavaModelAccess.saveJavaModel();
		this.getJavaFacade().reload();
		JavaModelAccess.setJavaModel(this.getJavaFacade().getResource());
	}

	protected org.emftext.language.java.classifiers.Interface addInterfaceToJavaModelResource(Resource modelRes,
			String javaIfcName, List<String> javaIfcNss) {
		var ifc = ClassifiersFactory.eINSTANCE.createInterface();
		ifc.setName(javaIfcName);
		var jrs = PcmJavaCPRUtils.addJavaClassifierIntoResource(modelRes, ifc, javaIfcNss);
		modelRes.getContents().addAll(jrs);
		return ifc;
	}

	protected org.emftext.language.java.classifiers.Class addClassToJavaModelResource(Resource modelRes,
			String className, List<String> classNss) {
		var cls = ClassifiersFactory.eINSTANCE.createClass();
		cls.setName(className);
		var jrs = PcmJavaCPRUtils.addJavaClassifierIntoResource(modelRes, cls, classNss);
		modelRes.getContents().addAll(jrs);
		return cls;
	}

	protected org.emftext.language.java.containers.Package addPackageToJavaModelResource(Resource modelRes,
			List<String> pacNss) {
		var pac = ContainersFactory.eINSTANCE.createPackage();
		pac.getNamespaces().addAll(pacNss);
		modelRes.getContents().add(pac);
		return pac;
	}

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
