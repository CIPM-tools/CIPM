package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.EqualityExpression;
import org.emftext.language.java.expressions.EqualityExpressionChild;
import org.emftext.language.java.operators.EqualityOperator;

public interface IEqualityExpressionInitialiser extends IAndExpressionChildInitialiser {
	@Override
	public EqualityExpression instantiate();

	public default boolean addEqualityOperator(EqualityExpression eqEx, EqualityOperator op) {
		if (op != null) {
			eqEx.getEqualityOperators().add(op);
			return eqEx.getEqualityOperators().contains(op);
		}
		return true;
	}

	public default boolean addChild(EqualityExpression eqEx, EqualityExpressionChild child) {
		if (child != null) {
			eqEx.getChildren().add(child);
			return eqEx.getChildren().contains(child);
		}
		return true;
	}

	public default boolean addEqualityOperators(EqualityExpression eqEx, EqualityOperator[] ops) {
		return this.doMultipleModifications(eqEx, ops, this::addEqualityOperator);
	}

	public default boolean addChildren(EqualityExpression eqEx, EqualityExpressionChild[] children) {
		return this.doMultipleModifications(eqEx, children, this::addChild);
	}
}