package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.MethodReferenceExpressionChild;
import org.emftext.language.java.expressions.PrimaryExpressionReferenceExpression;
import org.emftext.language.java.references.Reference;

import cipm.consistency.initialisers.jamopp.generics.ICallTypeArgumentableInitialiser;

public interface IPrimaryExpressionReferenceExpressionInitialiser
		extends ICallTypeArgumentableInitialiser, IMethodReferenceExpressionInitialiser {
	@Override
	public PrimaryExpressionReferenceExpression instantiate();

	public default boolean setChild(PrimaryExpressionReferenceExpression pere, MethodReferenceExpressionChild mrec) {
		pere.setChild(mrec);
		return (mrec == null && pere.getChild() == null) || pere.getChild().equals(mrec);
	}

	public default boolean setMethodReference(PrimaryExpressionReferenceExpression pere, Reference metRef) {
		pere.setMethodReference(metRef);
		return (metRef == null && pere.getMethodReference() == null) || pere.getMethodReference().equals(metRef);
	}
}
