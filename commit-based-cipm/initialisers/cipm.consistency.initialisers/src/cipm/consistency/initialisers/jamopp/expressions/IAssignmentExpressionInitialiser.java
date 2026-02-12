package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.AssignmentExpression;
import org.emftext.language.java.expressions.AssignmentExpressionChild;
import org.emftext.language.java.expressions.Expression;
import org.emftext.language.java.operators.AssignmentOperator;

public interface IAssignmentExpressionInitialiser extends IExpressionInitialiser {
	@Override
	public AssignmentExpression instantiate();

	public default boolean setAssignmentOperator(AssignmentExpression ae, AssignmentOperator op) {
		ae.setAssignmentOperator(op);
		return (op == null && ae.getAssignmentOperator() == null) || ae.getAssignmentOperator().equals(op);
	}

	public default boolean setChild(AssignmentExpression ae, AssignmentExpressionChild child) {
		ae.setChild(child);
		return (child == null && ae.getChild() == null) || ae.getChild().equals(child);
	}

	public default boolean setValue(AssignmentExpression ae, Expression val) {
		ae.setValue(val);
		return (val == null && ae.getValue() == null) || ae.getValue().equals(val);
	}
}
