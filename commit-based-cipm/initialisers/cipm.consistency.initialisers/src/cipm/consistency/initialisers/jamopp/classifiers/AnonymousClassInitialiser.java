package cipm.consistency.initialisers.jamopp.classifiers;

import org.emftext.language.java.classifiers.ClassifiersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.classifiers.AnonymousClass;

public class AnonymousClassInitialiser extends AbstractInitialiserBase implements IAnonymousClassInitialiser {
	@Override
	public IAnonymousClassInitialiser newInitialiser() {
		return new AnonymousClassInitialiser();
	}

	@Override
	public AnonymousClass instantiate() {
		return ClassifiersFactory.eINSTANCE.createAnonymousClass();
	}
}