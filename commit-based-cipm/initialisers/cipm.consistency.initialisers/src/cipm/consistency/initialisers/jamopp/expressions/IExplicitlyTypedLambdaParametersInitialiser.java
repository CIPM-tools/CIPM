package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExplicitlyTypedLambdaParameters;

public interface IExplicitlyTypedLambdaParametersInitialiser extends ILambdaParametersInitialiser {
	@Override
	public ExplicitlyTypedLambdaParameters instantiate();
}
