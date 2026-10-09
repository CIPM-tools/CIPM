package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.LiteralsFactory;
import org.emftext.language.java.literals.Super;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class SuperInitialiser extends AbstractInitialiserBase implements ISuperInitialiser {
	@Override
	public ISuperInitialiser newInitialiser() {
		return new SuperInitialiser();
	}

	@Override
	public Super instantiate() {
		return LiteralsFactory.eINSTANCE.createSuper();
	}
}