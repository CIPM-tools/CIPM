package cipm.consistency.initialisers.jamopp.classifiers;

import org.emftext.language.java.classifiers.Annotation;

public interface IAnnotationInitialiser extends IConcreteClassifierInitialiser {
	@Override
	public Annotation instantiate();
}
