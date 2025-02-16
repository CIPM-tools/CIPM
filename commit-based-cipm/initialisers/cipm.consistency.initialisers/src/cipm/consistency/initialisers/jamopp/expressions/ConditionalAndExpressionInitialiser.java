package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ConditionalAndExpression;
import org.emftext.language.java.expressions.ExpressionsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ConditionalAndExpressionInitialiser extends AbstractInitialiserBase
		implements IConditionalAndExpressionInitialiser {
	@Override
	public IConditionalAndExpressionInitialiser newInitialiser() {
		return new ConditionalAndExpressionInitialiser();
	}

	@Override
	public ConditionalAndExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createConditionalAndExpression();
	}
}