package cipm.consistency.initialisers.jamopp.instantiations;

import org.emftext.language.java.instantiations.ExplicitConstructorCall;
import org.emftext.language.java.instantiations.InstantiationsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ExplicitConstructorCallInitialiser extends AbstractInitialiserBase
		implements IExplicitConstructorCallInitialiser {
	@Override
	public IExplicitConstructorCallInitialiser newInitialiser() {
		return new ExplicitConstructorCallInitialiser();
	}

	@Override
	public ExplicitConstructorCall instantiate() {
		return InstantiationsFactory.eINSTANCE.createExplicitConstructorCall();
	}
}