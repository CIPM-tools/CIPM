package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.EqualityExpressionChild;

public interface IEqualityExpressionChildInitialiser extends IAndExpressionChildInitialiser {
	@Override
	public EqualityExpressionChild instantiate();

}