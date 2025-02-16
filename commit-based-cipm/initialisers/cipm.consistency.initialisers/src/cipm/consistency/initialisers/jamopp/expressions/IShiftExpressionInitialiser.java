package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ShiftExpression;
import org.emftext.language.java.expressions.ShiftExpressionChild;
import org.emftext.language.java.operators.ShiftOperator;

public interface IShiftExpressionInitialiser extends IRelationExpressionChildInitialiser {
	@Override
	public ShiftExpression instantiate();

	public default boolean addShiftOperator(ShiftExpression se, ShiftOperator op) {
		if (op != null) {
			se.getShiftOperators().add(op);
			return se.getShiftOperators().contains(op);
		}
		return true;
	}

	public default boolean addShiftOperators(ShiftExpression se, ShiftOperator[] ops) {
		return this.doMultipleModifications(se, ops, this::addShiftOperator);
	}

	public default boolean addChild(ShiftExpression se, ShiftExpressionChild child) {
		if (child != null) {
			se.getChildren().add(child);
			return se.getChildren().contains(child);
		}
		return true;
	}

	public default boolean addChildren(ShiftExpression se, ShiftExpressionChild[] children) {
		return this.doMultipleModifications(se, children, this::addChild);
	}
}