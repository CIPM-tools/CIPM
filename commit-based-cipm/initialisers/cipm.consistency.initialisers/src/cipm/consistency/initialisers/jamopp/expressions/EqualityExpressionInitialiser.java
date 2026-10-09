package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.EqualityExpression;
import org.emftext.language.java.expressions.ExpressionsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class EqualityExpressionInitialiser extends AbstractInitialiserBase implements IEqualityExpressionInitialiser {
	@Override
	public IEqualityExpressionInitialiser newInitialiser() {
		return new EqualityExpressionInitialiser();
	}

	@Override
	public EqualityExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createEqualityExpression();
	}
}