package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.ModifiersFactory;
import org.emftext.language.java.modifiers.Static;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class StaticInitialiser extends AbstractInitialiserBase implements IStaticInitialiser {
	@Override
	public IStaticInitialiser newInitialiser() {
		return new StaticInitialiser();
	}

	@Override
	public Static instantiate() {
		return ModifiersFactory.eINSTANCE.createStatic();
	}
}