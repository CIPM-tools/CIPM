package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.CastExpression;
import org.emftext.language.java.expressions.ExpressionsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class CastExpressionInitialiser extends AbstractInitialiserBase implements ICastExpressionInitialiser {
	@Override
	public ICastExpressionInitialiser newInitialiser() {
		return new CastExpressionInitialiser();
	}

	@Override
	public CastExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createCastExpression();
	}
}