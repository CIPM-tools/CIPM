package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.StatementListContainer;
import org.emftext.language.java.statements.SwitchCase;

public interface ISwitchCaseInitialiser extends IStatementListContainerInitialiser {
	@Override
	public SwitchCase instantiate();

	@Override
	public default boolean canContainStatements(StatementListContainer slc) {
		return true;
	}
}
