package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.expressions.NestedExpression;

import cipm.consistency.initialisers.jamopp.references.IReferenceInitialiser;

public interface INestedExpressionInitialiser extends IReferenceInitialiser {
	@Override
	public NestedExpression instantiate();

	public default boolean setExpression(NestedExpression ne, Expression expr) {
		ne.setExpression(expr);
		return (expr == null && ne.getExpression() == null) || ne.getExpression().equals(expr);
	}
}
