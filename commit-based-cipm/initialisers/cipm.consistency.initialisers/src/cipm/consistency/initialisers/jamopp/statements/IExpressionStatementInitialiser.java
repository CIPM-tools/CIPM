package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.ExpressionStatement;

public interface IExpressionStatementInitialiser extends IStatementInitialiser {
	@Override
	public ExpressionStatement instantiate();

	public default boolean setExpression(ExpressionStatement es, Expression expr) {
		es.setExpression(expr);
		return (expr == null && es.getExpression() == null) || es.getExpression().equals(expr);
	}
}
