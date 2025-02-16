package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.AssignmentPlus;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class AssignmentPlusInitialiser extends AbstractInitialiserBase implements IAssignmentPlusInitialiser {
	@Override
	public IAssignmentPlusInitialiser newInitialiser() {
		return new AssignmentPlusInitialiser();
	}

	@Override
	public AssignmentPlus instantiate() {
		return OperatorsFactory.eINSTANCE.createAssignmentPlus();
	}
}