package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.DefaultSwitchRule;

public interface IDefaultSwitchRuleInitialiser extends ISwitchRuleInitialiser {
	@Override
	public DefaultSwitchRule instantiate();

}
