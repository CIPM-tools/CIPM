package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ConditionalExpressionChild;

public interface IConditionalExpressionChildInitialiser extends IAssignmentExpressionChildInitialiser {
	@Override
	public ConditionalExpressionChild instantiate();

}
