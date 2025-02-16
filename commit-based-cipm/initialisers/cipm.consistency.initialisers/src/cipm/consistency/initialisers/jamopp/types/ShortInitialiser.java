package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.Short;
import org.emftext.language.java.types.TypesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ShortInitialiser extends AbstractInitialiserBase implements IShortInitialiser {
	@Override
	public IShortInitialiser newInitialiser() {
		return new ShortInitialiser();
	}

	@Override
	public Short instantiate() {
		return TypesFactory.eINSTANCE.createShort();
	}
}