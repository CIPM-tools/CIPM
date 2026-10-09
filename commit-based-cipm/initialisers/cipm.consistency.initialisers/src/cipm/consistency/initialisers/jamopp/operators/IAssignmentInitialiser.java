package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.Assignment;

public interface IAssignmentInitialiser extends IAssignmentOperatorInitialiser {
	@Override
	public Assignment instantiate();

}