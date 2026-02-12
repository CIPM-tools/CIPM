package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.GenericsFactory;
import org.emftext.language.java.generics.UnknownTypeArgument;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class UnknownTypeArgumentInitialiser extends AbstractInitialiserBase implements IUnknownTypeArgumentInitialiser {
	@Override
	public IUnknownTypeArgumentInitialiser newInitialiser() {
		return new UnknownTypeArgumentInitialiser();
	}

	@Override
	public UnknownTypeArgument instantiate() {
		return GenericsFactory.eINSTANCE.createUnknownTypeArgument();
	}
}