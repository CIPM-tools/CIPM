package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.AssignmentMinus;

public interface IAssignmentMinusInitialiser extends IAssignmentOperatorInitialiser {
	@Override
	public AssignmentMinus instantiate();

}
