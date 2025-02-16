package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.Break;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class BreakInitialiser extends AbstractInitialiserBase implements IBreakInitialiser {
	@Override
	public IBreakInitialiser newInitialiser() {
		return new BreakInitialiser();
	}

	@Override
	public Break instantiate() {
		return StatementsFactory.eINSTANCE.createBreak();
	}
}