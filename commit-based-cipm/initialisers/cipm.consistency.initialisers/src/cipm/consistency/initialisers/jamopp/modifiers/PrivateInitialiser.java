package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.ModifiersFactory;
import org.emftext.language.java.modifiers.Private;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class PrivateInitialiser extends AbstractInitialiserBase implements IPrivateInitialiser {
	@Override
	public IPrivateInitialiser newInitialiser() {
		return new PrivateInitialiser();
	}

	@Override
	public Private instantiate() {
		return ModifiersFactory.eINSTANCE.createPrivate();
	}
}