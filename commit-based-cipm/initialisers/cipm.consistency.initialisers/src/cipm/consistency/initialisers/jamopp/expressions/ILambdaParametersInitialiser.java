package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.LambdaParameters;

import cipm.consistency.initialisers.jamopp.parameters.IParametrizableInitialiser;

public interface ILambdaParametersInitialiser extends IParametrizableInitialiser {
	@Override
	public LambdaParameters instantiate();

}
