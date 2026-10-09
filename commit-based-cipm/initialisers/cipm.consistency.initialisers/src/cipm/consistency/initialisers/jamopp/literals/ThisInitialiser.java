package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.LiteralsFactory;
import org.emftext.language.java.literals.This;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ThisInitialiser extends AbstractInitialiserBase implements IThisInitialiser {
	@Override
	public IThisInitialiser newInitialiser() {
		return new ThisInitialiser();
	}

	@Override
	public This instantiate() {
		return LiteralsFactory.eINSTANCE.createThis();
	}
}