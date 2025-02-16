package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.AdditiveExpression;
import org.emftext.language.java.expressions.AdditiveExpressionChild;
import org.emftext.language.java.operators.AdditiveOperator;

public interface IAdditiveExpressionInitialiser extends IShiftExpressionChildInitialiser {
	@Override
	public AdditiveExpression instantiate();

	public default boolean addAdditiveOperator(AdditiveExpression ae, AdditiveOperator op) {
		if (op != null) {
			ae.getAdditiveOperators().add(op);
			return ae.getAdditiveOperators().contains(op);
		}
		return true;
	}

	public default boolean addAdditiveOperators(AdditiveExpression ae, AdditiveOperator[] ops) {
		return this.doMultipleModifications(ae, ops, this::addAdditiveOperator);
	}

	public default boolean addChild(AdditiveExpression ae, AdditiveExpressionChild child) {
		if (child != null) {
			ae.getChildren().add(child);
			return ae.getChildren().contains(child);
		}
		return true;
	}

	public default boolean addChildren(AdditiveExpression ae, AdditiveExpressionChild[] children) {
		return this.doMultipleModifications(ae, children, this::addChild);
	}
}