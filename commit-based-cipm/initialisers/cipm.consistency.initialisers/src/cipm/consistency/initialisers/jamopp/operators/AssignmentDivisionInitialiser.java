package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.AssignmentDivision;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class AssignmentDivisionInitialiser extends AbstractInitialiserBase implements IAssignmentDivisionInitialiser {
	@Override
	public IAssignmentDivisionInitialiser newInitialiser() {
		return new AssignmentDivisionInitialiser();
	}

	@Override
	public AssignmentDivision instantiate() {
		return OperatorsFactory.eINSTANCE.createAssignmentDivision();
	}
}