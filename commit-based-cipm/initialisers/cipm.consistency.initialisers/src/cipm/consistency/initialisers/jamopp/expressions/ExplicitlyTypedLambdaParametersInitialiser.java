package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExplicitlyTypedLambdaParameters;
import org.emftext.language.java.expressions.ExpressionsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ExplicitlyTypedLambdaParametersInitialiser extends AbstractInitialiserBase
		implements IExplicitlyTypedLambdaParametersInitialiser {
	@Override
	public IExplicitlyTypedLambdaParametersInitialiser newInitialiser() {
		return new ExplicitlyTypedLambdaParametersInitialiser();
	}

	@Override
	public ExplicitlyTypedLambdaParameters instantiate() {
		return ExpressionsFactory.eINSTANCE.createExplicitlyTypedLambdaParameters();
	}
}