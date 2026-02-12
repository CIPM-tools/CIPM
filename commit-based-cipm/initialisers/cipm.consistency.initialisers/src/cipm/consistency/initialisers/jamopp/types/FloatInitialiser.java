package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.Float;
import org.emftext.language.java.types.TypesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class FloatInitialiser extends AbstractInitialiserBase implements IFloatInitialiser {
	@Override
	public IFloatInitialiser newInitialiser() {
		return new FloatInitialiser();
	}

	@Override
	public Float instantiate() {
		return TypesFactory.eINSTANCE.createFloat();
	}
}