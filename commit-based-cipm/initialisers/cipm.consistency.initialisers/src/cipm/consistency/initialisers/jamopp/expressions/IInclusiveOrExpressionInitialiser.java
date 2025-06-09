package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.InclusiveOrExpression;
import org.emftext.language.java.expressions.InclusiveOrExpressionChild;

public interface IInclusiveOrExpressionInitialiser extends IConditionalAndExpressionChildInitialiser {
	@Override
	public InclusiveOrExpression instantiate();

	public default boolean addChild(InclusiveOrExpression ioe, InclusiveOrExpressionChild child) {
		if (child != null) {
			ioe.getChildren().add(child);
			return ioe.getChildren().contains(child);
		}
		return true;
	}

	public default boolean addChildren(InclusiveOrExpression ioe, InclusiveOrExpressionChild[] children) {
		return this.doMultipleModifications(ioe, children, this::addChild);
	}
}