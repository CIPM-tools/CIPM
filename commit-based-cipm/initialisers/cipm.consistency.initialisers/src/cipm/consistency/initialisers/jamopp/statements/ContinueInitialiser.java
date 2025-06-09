package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.Continue;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ContinueInitialiser extends AbstractInitialiserBase implements IContinueInitialiser {
	@Override
	public IContinueInitialiser newInitialiser() {
		return new ContinueInitialiser();
	}

	@Override
	public Continue instantiate() {
		return StatementsFactory.eINSTANCE.createContinue();
	}
}