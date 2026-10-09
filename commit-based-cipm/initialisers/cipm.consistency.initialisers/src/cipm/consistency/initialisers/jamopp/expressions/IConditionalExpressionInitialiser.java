package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.AssignmentExpressionChild;
import org.emftext.language.java.expressions.ConditionalExpression;
import org.emftext.language.java.expressions.ConditionalExpressionChild;
import org.emftext.language.java.expressions.Expression;

public interface IConditionalExpressionInitialiser extends IAssignmentExpressionChildInitialiser {
	@Override
	public ConditionalExpression instantiate();

	public default boolean setChild(ConditionalExpression ce, ConditionalExpressionChild child) {
		ce.setChild(child);
		return (child == null && ce.getChild() == null) || ce.getChild().equals(child);
	}

	public default boolean setExpressionChild(ConditionalExpression ce, AssignmentExpressionChild exprChild) {
		ce.setExpressionChild(exprChild);
		return (exprChild == null && ce.getExpressionElse() == null) || ce.getExpressionElse().equals(exprChild);
	}

	public default boolean setExpressionIf(ConditionalExpression ce, Expression exprIf) {
		ce.setExpressionIf(exprIf);
		return (exprIf == null && ce.getExpressionIf() == null) || ce.getExpressionIf().equals(exprIf);
	}

	public default boolean setGeneralExpressionElse(ConditionalExpression ce, Expression generalExprElse) {
		ce.setGeneralExpressionElse(generalExprElse);
		return (generalExprElse == null && ce.getGeneralExpressionElse() == null)
				|| ce.getGeneralExpressionElse().equals(generalExprElse);
	}
}
