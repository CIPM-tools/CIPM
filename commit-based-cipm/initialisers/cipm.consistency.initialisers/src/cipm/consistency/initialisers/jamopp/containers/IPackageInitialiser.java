package cipm.consistency.initialisers.jamopp.containers;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.commons.NamedElement;
import org.emftext.language.java.containers.Module;
import org.emftext.language.java.containers.Package;

import cipm.consistency.initialisers.jamopp.references.IReferenceableElementInitialiser;

/**
 * An interface meant to be implemented by {@link IInitialiser} implementors
 * that are supposed to create {@link Package} instances. <br>
 * <br>
 * <b>Note: Package names {@code package.getName()} is supposed to be left
 * empty. Changing it HAS NO EFFECT on the name of the package in similarity
 * checking. Package's name consists of the concatenation of its namespaces with
 * dots ".". Therefore, use
 * {@link #addNamespace(org.emftext.language.java.commons.NamespaceAwareElement, String)}
 * to modify its name.</b>
 * 
 * @author Alp Torac Genc
 */
public interface IPackageInitialiser extends IJavaRootInitialiser, IReferenceableElementInitialiser {
	@Override
	public Package instantiate();

	/**
	 * @return False, because the return value of {@code package.getName()} is
	 *         determined by the namespaces of the package instead.
	 * 
	 * @see {@link IPackageInitialiser}
	 */
	@Override
	public default boolean canSetName(NamedElement ne) {
		return false;
	}

	public default boolean setModule(Package pac, Module mod) {
		pac.setModule(mod);
		return (mod == null && pac.getModule() == null) || pac.getModule().equals(mod);
	}

	public default boolean addClassifier(Package pac, ConcreteClassifier cls) {
		if (cls != null) {
			pac.getClassifiers().add(cls);
			return pac.getClassifiers().contains(cls);
		}
		return true;
	}

	public default boolean addClassifiers(Package pac, ConcreteClassifier[] clss) {
		return this.doMultipleModifications(pac, clss, this::addClassifier);
	}
}