package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.Char;
import org.emftext.language.java.types.TypesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class CharInitialiser extends AbstractInitialiserBase implements ICharInitialiser {
	@Override
	public ICharInitialiser newInitialiser() {
		return new CharInitialiser();
	}

	@Override
	public Char instantiate() {
		return TypesFactory.eINSTANCE.createChar();
	}
}