package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExpressionsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.expressions.AdditiveExpression;

public class AdditiveExpressionInitialiser extends AbstractInitialiserBase implements IAdditiveExpressionInitialiser {
	@Override
	public IAdditiveExpressionInitialiser newInitialiser() {
		return new AdditiveExpressionInitialiser();
	}

	@Override
	public AdditiveExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createAdditiveExpression();
	}
}
