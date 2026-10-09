package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.Expression;

import cipm.consistency.initialisers.jamopp.arrays.IArrayInitializationValueInitialiser;

public interface IExpressionInitialiser extends IArrayInitializationValueInitialiser, ILambdaBodyInitialiser {
	@Override
	public Expression instantiate();
}
