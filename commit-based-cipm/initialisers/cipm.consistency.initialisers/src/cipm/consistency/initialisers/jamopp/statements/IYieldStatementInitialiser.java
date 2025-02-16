package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.statements.YieldStatement;

public interface IYieldStatementInitialiser extends IStatementInitialiser {
	@Override
	public YieldStatement instantiate();

	public default boolean setYieldExpression(YieldStatement ys, Expression yieldExpr) {
		ys.setYieldExpression(yieldExpr);
		return (yieldExpr == null && ys.getYieldExpression() == null) || ys.getYieldExpression().equals(yieldExpr);
	}
}
