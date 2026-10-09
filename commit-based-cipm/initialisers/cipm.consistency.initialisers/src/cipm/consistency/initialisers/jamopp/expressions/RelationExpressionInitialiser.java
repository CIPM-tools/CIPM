package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExpressionsFactory;
import org.emftext.language.java.expressions.RelationExpression;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class RelationExpressionInitialiser extends AbstractInitialiserBase implements IRelationExpressionInitialiser {
	@Override
	public IRelationExpressionInitialiser newInitialiser() {
		return new RelationExpressionInitialiser();
	}

	@Override
	public RelationExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createRelationExpression();
	}
}