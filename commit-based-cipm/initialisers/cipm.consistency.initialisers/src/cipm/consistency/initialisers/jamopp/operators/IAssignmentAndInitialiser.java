package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.AssignmentAnd;

public interface IAssignmentAndInitialiser extends IAssignmentOperatorInitialiser {
	@Override
	public AssignmentAnd instantiate();

}
