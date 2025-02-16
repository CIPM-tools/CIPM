package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.RelationExpression;
import org.emftext.language.java.expressions.RelationExpressionChild;
import org.emftext.language.java.operators.RelationOperator;

public interface IRelationExpressionInitialiser extends IInstanceOfExpressionChildInitialiser {
	@Override
	public RelationExpression instantiate();

	public default boolean addRelationOperator(RelationExpression re, RelationOperator op) {
		if (op != null) {
			re.getRelationOperators().add(op);
			return re.getRelationOperators().contains(op);
		}
		return true;
	}

	public default boolean addRelationOperators(RelationExpression re, RelationOperator[] ops) {
		return this.doMultipleModifications(re, ops, this::addRelationOperator);
	}

	public default boolean addChild(RelationExpression cae, RelationExpressionChild child) {
		if (child != null) {
			cae.getChildren().add(child);
			return cae.getChildren().contains(child);
		}
		return true;
	}

	public default boolean addChildren(RelationExpression cae, RelationExpressionChild[] children) {
		return this.doMultipleModifications(cae, children, this::addChild);
	}
}