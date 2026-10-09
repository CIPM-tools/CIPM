package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.IdentifierReference;
import org.emftext.language.java.references.ReferencesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class IdentifierReferenceInitialiser extends AbstractInitialiserBase implements IIdentifierReferenceInitialiser {
	@Override
	public IIdentifierReferenceInitialiser newInitialiser() {
		return new IdentifierReferenceInitialiser();
	}

	@Override
	public IdentifierReference instantiate() {
		return ReferencesFactory.eINSTANCE.createIdentifierReference();
	}
}