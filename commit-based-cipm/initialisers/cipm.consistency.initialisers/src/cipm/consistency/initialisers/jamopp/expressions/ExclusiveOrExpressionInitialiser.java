package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExclusiveOrExpression;
import org.emftext.language.java.expressions.ExpressionsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ExclusiveOrExpressionInitialiser extends AbstractInitialiserBase
		implements IExclusiveOrExpressionInitialiser {
	@Override
	public IExclusiveOrExpressionInitialiser newInitialiser() {
		return new ExclusiveOrExpressionInitialiser();
	}

	@Override
	public ExclusiveOrExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createExclusiveOrExpression();
	}
}