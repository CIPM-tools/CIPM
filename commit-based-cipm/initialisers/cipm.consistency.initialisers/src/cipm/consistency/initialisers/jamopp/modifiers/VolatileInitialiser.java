package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.ModifiersFactory;
import org.emftext.language.java.modifiers.Volatile;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class VolatileInitialiser extends AbstractInitialiserBase implements IVolatileInitialiser {
	@Override
	public IVolatileInitialiser newInitialiser() {
		return new VolatileInitialiser();
	}

	@Override
	public Volatile instantiate() {
		return ModifiersFactory.eINSTANCE.createVolatile();
	}
}