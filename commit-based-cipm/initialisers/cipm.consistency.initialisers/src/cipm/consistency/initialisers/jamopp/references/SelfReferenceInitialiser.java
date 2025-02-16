package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.ReferencesFactory;
import org.emftext.language.java.references.SelfReference;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class SelfReferenceInitialiser extends AbstractInitialiserBase implements ISelfReferenceInitialiser {
	@Override
	public ISelfReferenceInitialiser newInitialiser() {
		return new SelfReferenceInitialiser();
	}

	@Override
	public SelfReference instantiate() {
		return ReferencesFactory.eINSTANCE.createSelfReference();
	}
}