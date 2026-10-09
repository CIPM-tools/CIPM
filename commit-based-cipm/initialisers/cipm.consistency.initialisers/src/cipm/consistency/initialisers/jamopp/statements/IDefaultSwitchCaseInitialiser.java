package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.DefaultSwitchCase;

public interface IDefaultSwitchCaseInitialiser extends ISwitchCaseInitialiser {
	@Override
	public DefaultSwitchCase instantiate();

}
