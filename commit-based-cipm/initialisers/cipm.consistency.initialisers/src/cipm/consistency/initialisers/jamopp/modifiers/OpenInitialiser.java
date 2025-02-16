package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.ModifiersFactory;
import org.emftext.language.java.modifiers.Open;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class OpenInitialiser extends AbstractInitialiserBase implements IOpenInitialiser {
	@Override
	public IOpenInitialiser newInitialiser() {
		return new OpenInitialiser();
	}

	@Override
	public Open instantiate() {
		return ModifiersFactory.eINSTANCE.createOpen();
	}
}