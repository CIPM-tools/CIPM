package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.AndExpressionChild;

public interface IAndExpressionChildInitialiser extends IExclusiveOrExpressionChildInitialiser {
	@Override
	public AndExpressionChild instantiate();

}