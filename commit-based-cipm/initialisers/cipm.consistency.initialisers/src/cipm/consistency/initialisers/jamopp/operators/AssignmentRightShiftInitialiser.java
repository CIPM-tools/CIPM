package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.AssignmentRightShift;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class AssignmentRightShiftInitialiser extends AbstractInitialiserBase
		implements IAssignmentRightShiftInitialiser {
	@Override
	public IAssignmentRightShiftInitialiser newInitialiser() {
		return new AssignmentRightShiftInitialiser();
	}

	@Override
	public AssignmentRightShift instantiate() {
		return OperatorsFactory.eINSTANCE.createAssignmentRightShift();
	}
}