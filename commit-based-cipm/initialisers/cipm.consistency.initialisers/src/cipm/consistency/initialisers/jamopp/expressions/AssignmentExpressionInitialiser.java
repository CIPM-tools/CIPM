package cipm.consistency.initialisers.jamopp.expressions;

import org.emftext.language.java.expressions.ExpressionsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.expressions.AssignmentExpression;

public class AssignmentExpressionInitialiser extends AbstractInitialiserBase
		implements IAssignmentExpressionInitialiser {
	@Override
	public IAssignmentExpressionInitialiser newInitialiser() {
		return new AssignmentExpressionInitialiser();
	}

	@Override
	public AssignmentExpression instantiate() {
		return ExpressionsFactory.eINSTANCE.createAssignmentExpression();
	}
}