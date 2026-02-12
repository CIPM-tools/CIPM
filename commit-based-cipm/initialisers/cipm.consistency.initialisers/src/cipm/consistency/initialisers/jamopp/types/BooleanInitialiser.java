package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.Boolean;
import org.emftext.language.java.types.TypesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class BooleanInitialiser extends AbstractInitialiserBase implements IBooleanInitialiser {
	@Override
	public IBooleanInitialiser newInitialiser() {
		return new BooleanInitialiser();
	}

	@Override
	public Boolean instantiate() {
		return TypesFactory.eINSTANCE.createBoolean();
	}
}