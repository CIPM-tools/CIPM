package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.MultiplicativeExpressionChild;

public interface IMultiplicativeExpressionChildInitialiser extends IAdditiveExpressionChildInitialiser {
	@Override
	public MultiplicativeExpressionChild instantiate();

}