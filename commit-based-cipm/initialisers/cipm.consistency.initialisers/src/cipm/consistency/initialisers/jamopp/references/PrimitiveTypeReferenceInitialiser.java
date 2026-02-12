package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.PrimitiveTypeReference;
import org.emftext.language.java.references.ReferencesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class PrimitiveTypeReferenceInitialiser extends AbstractInitialiserBase
		implements IPrimitiveTypeReferenceInitialiser {
	@Override
	public IPrimitiveTypeReferenceInitialiser newInitialiser() {
		return new PrimitiveTypeReferenceInitialiser();
	}

	@Override
	public PrimitiveTypeReference instantiate() {
		return ReferencesFactory.eINSTANCE.createPrimitiveTypeReference();
	}
}