package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExpressionsFactory;
import org.emftext.language.java.expressions.UnaryExpression;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class UnaryExpressionInitialiser extends AbstractInitialiserBase implements IUnaryExpressionInitialiser {
	@Override
	public IUnaryExpressionInitialiser newInitialiser() {
		return new UnaryExpressionInitialiser();
	}

	@Override
	public UnaryExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createUnaryExpression();
	}
}