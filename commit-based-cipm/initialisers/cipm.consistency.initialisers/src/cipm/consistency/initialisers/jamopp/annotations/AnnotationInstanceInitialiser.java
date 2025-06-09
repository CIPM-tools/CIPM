package cipm.consistency.initialisers.jamopp.annotations;

import org.emftext.language.java.annotations.AnnotationsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.annotations.AnnotationInstance;

public class AnnotationInstanceInitialiser extends AbstractInitialiserBase implements IAnnotationInstanceInitialiser {
	@Override
	public AnnotationInstance instantiate() {
		var fac = AnnotationsFactory.eINSTANCE;
		return fac.createAnnotationInstance();
	}

	@Override
	public IAnnotationInstanceInitialiser newInitialiser() {
		return new AnnotationInstanceInitialiser();
	}
}
