package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExpressionList;
import org.emftext.language.java.expressions.ExpressionsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ExpressionListInitialiser extends AbstractInitialiserBase implements IExpressionListInitialiser {
	@Override
	public IExpressionListInitialiser newInitialiser() {
		return new ExpressionListInitialiser();
	}

	@Override
	public ExpressionList instantiate() {
		return ExpressionsFactory.eINSTANCE.createExpressionList();
	}
}