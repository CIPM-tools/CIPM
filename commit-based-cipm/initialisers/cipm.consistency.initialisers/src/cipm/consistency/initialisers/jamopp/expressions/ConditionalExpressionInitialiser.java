package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ConditionalExpression;
import org.emftext.language.java.expressions.ExpressionsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ConditionalExpressionInitialiser extends AbstractInitialiserBase
		implements IConditionalExpressionInitialiser {
	@Override
	public IConditionalExpressionInitialiser newInitialiser() {
		return new ConditionalExpressionInitialiser();
	}

	@Override
	public ConditionalExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createConditionalExpression();
	}
}