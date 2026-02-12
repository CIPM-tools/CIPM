package cipm.consistency.initialisers.jamopp.generics;

import org.emftext.language.java.generics.GenericsFactory;
import org.emftext.language.java.generics.SuperTypeArgument;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class SuperTypeArgumentInitialiser extends AbstractInitialiserBase implements ISuperTypeArgumentInitialiser {
	@Override
	public ISuperTypeArgumentInitialiser newInitialiser() {
		return new SuperTypeArgumentInitialiser();
	}

	@Override
	public SuperTypeArgument instantiate() {
		return GenericsFactory.eINSTANCE.createSuperTypeArgument();
	}
}