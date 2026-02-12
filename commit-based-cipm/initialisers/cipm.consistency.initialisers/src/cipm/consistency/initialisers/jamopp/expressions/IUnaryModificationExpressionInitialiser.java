package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.UnaryModificationExpression;
import org.emftext.language.java.expressions.UnaryModificationExpressionChild;
import org.emftext.language.java.operators.UnaryModificationOperator;

public interface IUnaryModificationExpressionInitialiser extends IUnaryExpressionChildInitialiser {
	@Override
	public UnaryModificationExpression instantiate();

	public default boolean setChild(UnaryModificationExpression ume, UnaryModificationExpressionChild child) {
		ume.setChild(child);
		return (child == null && ume.getChild() == null) || ume.getChild().equals(child);
	}

	public default boolean setOperator(UnaryModificationExpression ume, UnaryModificationOperator op) {
		ume.setOperator(op);
		return (op == null && ume.getOperator() == null) || ume.getOperator().equals(op);
	}
}