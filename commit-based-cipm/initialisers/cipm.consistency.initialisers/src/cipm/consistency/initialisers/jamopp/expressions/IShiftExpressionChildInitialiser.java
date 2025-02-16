package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ShiftExpressionChild;

public interface IShiftExpressionChildInitialiser extends IRelationExpressionChildInitialiser {
	@Override
	public ShiftExpressionChild instantiate();

}