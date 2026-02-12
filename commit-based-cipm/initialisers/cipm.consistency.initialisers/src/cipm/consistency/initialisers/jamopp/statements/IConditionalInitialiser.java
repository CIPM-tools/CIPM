package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.Conditional;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IConditionalInitialiser extends ICommentableInitialiser {
	@Override
	public Conditional instantiate();

	public default boolean setCondition(Conditional cond, Expression condExpr) {
		cond.setCondition(condExpr);
		return (condExpr == null && cond.getCondition() == null) || cond.getCondition().equals(condExpr);
	}
}
