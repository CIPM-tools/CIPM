package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ConditionalAndExpression;
import org.emftext.language.java.expressions.ConditionalAndExpressionChild;

public interface IConditionalAndExpressionInitialiser extends IConditionalOrExpressionChildInitialiser {
	@Override
	public ConditionalAndExpression instantiate();

	public default boolean addChild(ConditionalAndExpression cae, ConditionalAndExpressionChild child) {
		if (child != null) {
			cae.getChildren().add(child);
			return cae.getChildren().contains(child);
		}
		return true;
	}

	public default boolean addChildren(ConditionalAndExpression cae, ConditionalAndExpressionChild[] children) {
		return this.doMultipleModifications(cae, children, this::addChild);
	}
}