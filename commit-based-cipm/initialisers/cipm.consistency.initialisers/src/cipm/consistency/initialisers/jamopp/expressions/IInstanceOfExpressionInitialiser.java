package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.InstanceOfExpression;
import org.emftext.language.java.expressions.InstanceOfExpressionChild;

import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface IInstanceOfExpressionInitialiser
		extends IEqualityExpressionChildInitialiser, ITypedElementInitialiser {
	@Override
	public InstanceOfExpression instantiate();

	public default boolean setChild(InstanceOfExpression ioe, InstanceOfExpressionChild child) {
		ioe.setChild(child);
		return (child == null && ioe.getChild() == null) || ioe.getChild().equals(child);
	}
}
