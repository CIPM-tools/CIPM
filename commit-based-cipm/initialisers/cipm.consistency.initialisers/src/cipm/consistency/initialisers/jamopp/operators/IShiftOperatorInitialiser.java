package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.ShiftOperator;

public interface IShiftOperatorInitialiser extends IOperatorInitialiser {
	@Override
	public ShiftOperator instantiate();

}
