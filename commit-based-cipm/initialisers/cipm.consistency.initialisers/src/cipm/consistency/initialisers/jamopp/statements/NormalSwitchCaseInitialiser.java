package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.NormalSwitchCase;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class NormalSwitchCaseInitialiser extends AbstractInitialiserBase implements INormalSwitchCaseInitialiser {
	@Override
	public INormalSwitchCaseInitialiser newInitialiser() {
		return new NormalSwitchCaseInitialiser();
	}

	@Override
	public NormalSwitchCase instantiate() {
		return StatementsFactory.eINSTANCE.createNormalSwitchCase();
	}
}