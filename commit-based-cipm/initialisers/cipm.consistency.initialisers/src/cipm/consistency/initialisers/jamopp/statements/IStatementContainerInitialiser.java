package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementContainer;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IStatementContainerInitialiser extends ICommentableInitialiser {
	@Override
	public StatementContainer instantiate();

	public default boolean setStatement(StatementContainer sc, Statement st) {
		sc.setStatement(st);
		return (st == null && sc.getStatement() == null) || sc.getStatement().equals(st);
	}
}
