package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.MultiplicativeExpression;
import org.emftext.language.java.expressions.MultiplicativeExpressionChild;
import org.emftext.language.java.operators.MultiplicativeOperator;

public interface IMultiplicativeExpressionInitialiser extends IAdditiveExpressionChildInitialiser {
	@Override
	public MultiplicativeExpression instantiate();

	public default boolean addMultiplicativeOperator(MultiplicativeExpression ae, MultiplicativeOperator op) {
		if (op != null) {
			ae.getMultiplicativeOperators().add(op);
			return ae.getMultiplicativeOperators().contains(op);
		}
		return true;
	}

	public default boolean addMultiplicativeOperators(MultiplicativeExpression ae, MultiplicativeOperator[] ops) {
		return this.doMultipleModifications(ae, ops, this::addMultiplicativeOperator);
	}

	public default boolean addChild(MultiplicativeExpression ae, MultiplicativeExpressionChild child) {
		if (child != null) {
			ae.getChildren().add(child);
			return ae.getChildren().contains(child);
		}
		return true;
	}

	public default boolean addChildren(MultiplicativeExpression ae, MultiplicativeExpressionChild[] children) {
		return this.doMultipleModifications(ae, children, this::addChild);
	}
}