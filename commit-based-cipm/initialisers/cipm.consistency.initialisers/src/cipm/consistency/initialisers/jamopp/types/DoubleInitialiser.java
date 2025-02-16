package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.Double;
import org.emftext.language.java.types.TypesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class DoubleInitialiser extends AbstractInitialiserBase implements IDoubleInitialiser {
	@Override
	public IDoubleInitialiser newInitialiser() {
		return new DoubleInitialiser();
	}

	@Override
	public Double instantiate() {
		return TypesFactory.eINSTANCE.createDouble();
	}
}