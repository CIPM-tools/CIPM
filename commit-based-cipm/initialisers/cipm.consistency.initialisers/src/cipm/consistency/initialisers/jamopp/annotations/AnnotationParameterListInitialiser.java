package cipm.consistency.initialisers.jamopp.annotations;

import org.emftext.language.java.annotations.AnnotationsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.annotations.AnnotationParameterList;

public class AnnotationParameterListInitialiser extends AbstractInitialiserBase
		implements IAnnotationParameterListInitialiser {
	@Override
	public IAnnotationParameterListInitialiser newInitialiser() {
		return new AnnotationParameterListInitialiser();
	}

	@Override
	public AnnotationParameterList instantiate() {
		return AnnotationsFactory.eINSTANCE.createAnnotationParameterList();
	}
}
