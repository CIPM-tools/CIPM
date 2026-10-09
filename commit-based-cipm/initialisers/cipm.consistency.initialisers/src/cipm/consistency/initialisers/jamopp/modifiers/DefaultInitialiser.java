package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.Default;
import org.emftext.language.java.modifiers.ModifiersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class DefaultInitialiser extends AbstractInitialiserBase implements IDefaultInitialiser {
	@Override
	public IDefaultInitialiser newInitialiser() {
		return new DefaultInitialiser();
	}

	@Override
	public Default instantiate() {
		return ModifiersFactory.eINSTANCE.createDefault();
	}
}