package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.statements.Switch;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class SwitchInitialiser extends AbstractInitialiserBase implements ISwitchInitialiser {
	@Override
	public ISwitchInitialiser newInitialiser() {
		return new SwitchInitialiser();
	}

	@Override
	public Switch instantiate() {
		return StatementsFactory.eINSTANCE.createSwitch();
	}
}