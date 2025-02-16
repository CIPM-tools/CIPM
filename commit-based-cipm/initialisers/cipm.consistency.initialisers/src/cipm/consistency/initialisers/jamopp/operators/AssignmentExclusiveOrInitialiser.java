package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.AssignmentExclusiveOr;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class AssignmentExclusiveOrInitialiser extends AbstractInitialiserBase
		implements IAssignmentExclusiveOrInitialiser {
	@Override
	public IAssignmentExclusiveOrInitialiser newInitialiser() {
		return new AssignmentExclusiveOrInitialiser();
	}

	@Override
	public AssignmentExclusiveOr instantiate() {
		return OperatorsFactory.eINSTANCE.createAssignmentExclusiveOr();
	}
}