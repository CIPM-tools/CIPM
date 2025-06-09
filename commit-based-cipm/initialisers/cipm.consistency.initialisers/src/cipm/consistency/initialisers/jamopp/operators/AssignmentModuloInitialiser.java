package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.AssignmentModulo;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class AssignmentModuloInitialiser extends AbstractInitialiserBase implements IAssignmentModuloInitialiser {
	@Override
	public IAssignmentModuloInitialiser newInitialiser() {
		return new AssignmentModuloInitialiser();
	}

	@Override
	public AssignmentModulo instantiate() {
		return OperatorsFactory.eINSTANCE.createAssignmentModulo();
	}
}