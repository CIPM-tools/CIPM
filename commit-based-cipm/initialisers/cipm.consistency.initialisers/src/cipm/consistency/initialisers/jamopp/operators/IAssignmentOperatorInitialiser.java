package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.AssignmentOperator;

public interface IAssignmentOperatorInitialiser extends IOperatorInitialiser {
	@Override
	public AssignmentOperator instantiate();

}
