package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.ModifiersFactory;
import org.emftext.language.java.modifiers.Native;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class NativeInitialiser extends AbstractInitialiserBase implements INativeInitialiser {
	@Override
	public INativeInitialiser newInitialiser() {
		return new NativeInitialiser();
	}

	@Override
	public Native instantiate() {
		return ModifiersFactory.eINSTANCE.createNative();
	}
}