package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.Condition;
import org.emftext.language.java.statements.Statement;

public interface IConditionInitialiser
		extends IConditionalInitialiser, IStatementInitialiser, IStatementContainerInitialiser {

	@Override
	public Condition instantiate();

	public default boolean setElseStatement(Condition cond, Statement elseSt) {
		cond.setElseStatement(elseSt);
		return (elseSt == null && cond.getElseStatement() == null) || cond.getElseStatement().equals(elseSt);
	}
}
