package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ConditionalOrExpressionChild;

public interface IConditionalOrExpressionChildInitialiser extends IConditionalExpressionChildInitialiser {
	@Override
	public ConditionalOrExpressionChild instantiate();

}