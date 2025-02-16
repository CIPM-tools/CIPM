package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.AndExpression;
import org.emftext.language.java.expressions.ExpressionsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class AndExpressionInitialiser extends AbstractInitialiserBase implements IAndExpressionInitialiser {
	@Override
	public IAndExpressionInitialiser newInitialiser() {
		return new AndExpressionInitialiser();
	}

	@Override
	public AndExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createAndExpression();
	}
}