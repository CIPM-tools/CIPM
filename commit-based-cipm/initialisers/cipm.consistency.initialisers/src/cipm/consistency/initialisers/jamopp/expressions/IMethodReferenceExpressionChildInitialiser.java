package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.MethodReferenceExpressionChild;

public interface IMethodReferenceExpressionChildInitialiser extends IUnaryModificationExpressionChildInitialiser {
	@Override
	public MethodReferenceExpressionChild instantiate();

}