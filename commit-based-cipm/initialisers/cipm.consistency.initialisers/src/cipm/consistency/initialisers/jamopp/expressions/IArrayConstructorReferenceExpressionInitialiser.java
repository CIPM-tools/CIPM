package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ArrayConstructorReferenceExpression;

import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface IArrayConstructorReferenceExpressionInitialiser
		extends IMethodReferenceExpressionInitialiser, ITypedElementInitialiser {
	@Override
	public ArrayConstructorReferenceExpression instantiate();
}
