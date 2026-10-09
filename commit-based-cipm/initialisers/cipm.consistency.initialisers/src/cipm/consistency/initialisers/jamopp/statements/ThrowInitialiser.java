package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.statements.Throw;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ThrowInitialiser extends AbstractInitialiserBase implements IThrowInitialiser {
	@Override
	public IThrowInitialiser newInitialiser() {
		return new ThrowInitialiser();
	}

	@Override
	public Throw instantiate() {
		return StatementsFactory.eINSTANCE.createThrow();
	}
}