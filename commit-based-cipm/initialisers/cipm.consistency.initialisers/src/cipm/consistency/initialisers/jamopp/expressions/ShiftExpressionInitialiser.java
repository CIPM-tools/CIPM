package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExpressionsFactory;
import org.emftext.language.java.expressions.ShiftExpression;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ShiftExpressionInitialiser extends AbstractInitialiserBase implements IShiftExpressionInitialiser {
	@Override
	public IShiftExpressionInitialiser newInitialiser() {
		return new ShiftExpressionInitialiser();
	}

	@Override
	public ShiftExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createShiftExpression();
	}
}