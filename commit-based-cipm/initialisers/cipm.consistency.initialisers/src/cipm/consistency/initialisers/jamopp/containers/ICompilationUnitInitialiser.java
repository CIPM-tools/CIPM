package cipm.consistency.initialisers.jamopp.containers;

import org.emftext.language.java.classifiers.ConcreteClassifier;
import org.emftext.language.java.containers.CompilationUnit;

public interface ICompilationUnitInitialiser extends IJavaRootInitialiser {
	@Override
	public CompilationUnit instantiate();

	public default boolean addClassifier(CompilationUnit cu, ConcreteClassifier cls) {
		if (cls != null) {
			cu.getClassifiers().add(cls);
			return cu.getClassifiers().contains(cls) && cu.getContainedClassifier(cls.getQualifiedName()) != null
					&& cu.getContainedClassifier(cls.getQualifiedName()).equals(cls);
		}
		return true;
	}

	public default boolean addClassifiers(CompilationUnit cu, ConcreteClassifier[] clss) {
		return this.doMultipleModifications(cu, clss, this::addClassifier);
	}
}
