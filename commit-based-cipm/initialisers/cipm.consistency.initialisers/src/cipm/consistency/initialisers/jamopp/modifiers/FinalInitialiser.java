package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.Final;
import org.emftext.language.java.modifiers.ModifiersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class FinalInitialiser extends AbstractInitialiserBase implements IFinalInitialiser {
	@Override
	public IFinalInitialiser newInitialiser() {
		return new FinalInitialiser();
	}

	@Override
	public Final instantiate() {
		return ModifiersFactory.eINSTANCE.createFinal();
	}
}