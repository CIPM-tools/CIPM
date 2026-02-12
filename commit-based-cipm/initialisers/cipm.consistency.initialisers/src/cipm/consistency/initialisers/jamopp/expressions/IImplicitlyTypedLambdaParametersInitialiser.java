package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ImplicitlyTypedLambdaParameters;

public interface IImplicitlyTypedLambdaParametersInitialiser extends ILambdaParametersInitialiser {
	@Override
	public ImplicitlyTypedLambdaParameters instantiate();
}
