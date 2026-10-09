package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.AndExpression;
import org.emftext.language.java.expressions.AndExpressionChild;

public interface IAndExpressionInitialiser extends IExclusiveOrExpressionChildInitialiser {
	@Override
	public AndExpression instantiate();

	public default boolean addChild(AndExpression ae, AndExpressionChild child) {
		if (child != null) {
			ae.getChildren().add(child);
			return ae.getChildren().contains(child);
		}
		return true;
	}

	public default boolean addChildren(AndExpression ae, AndExpressionChild[] children) {
		return this.doMultipleModifications(ae, children, this::addChild);
	}
}