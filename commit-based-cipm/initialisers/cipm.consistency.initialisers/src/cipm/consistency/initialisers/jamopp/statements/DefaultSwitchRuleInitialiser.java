package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.DefaultSwitchRule;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class DefaultSwitchRuleInitialiser extends AbstractInitialiserBase implements IDefaultSwitchRuleInitialiser {
	@Override
	public IDefaultSwitchRuleInitialiser newInitialiser() {
		return new DefaultSwitchRuleInitialiser();
	}

	@Override
	public DefaultSwitchRule instantiate() {
		return StatementsFactory.eINSTANCE.createDefaultSwitchRule();
	}
}