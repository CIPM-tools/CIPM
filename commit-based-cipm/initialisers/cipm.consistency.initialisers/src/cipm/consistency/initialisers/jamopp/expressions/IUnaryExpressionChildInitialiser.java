package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.UnaryExpressionChild;

public interface IUnaryExpressionChildInitialiser extends IMultiplicativeExpressionChildInitialiser {
	@Override
	public UnaryExpressionChild instantiate();

}