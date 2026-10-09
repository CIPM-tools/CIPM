package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExpressionsFactory;
import org.emftext.language.java.expressions.InstanceOfExpression;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class InstanceOfExpressionInitialiser extends AbstractInitialiserBase
		implements IInstanceOfExpressionInitialiser {
	@Override
	public IInstanceOfExpressionInitialiser newInitialiser() {
		return new InstanceOfExpressionInitialiser();
	}

	@Override
	public InstanceOfExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createInstanceOfExpression();
	}
}