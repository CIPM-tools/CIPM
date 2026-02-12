package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.ModifiersFactory;
import org.emftext.language.java.modifiers.Protected;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ProtectedInitialiser extends AbstractInitialiserBase implements IProtectedInitialiser {
	@Override
	public IProtectedInitialiser newInitialiser() {
		return new ProtectedInitialiser();
	}

	@Override
	public Protected instantiate() {
		return ModifiersFactory.eINSTANCE.createProtected();
	}
}