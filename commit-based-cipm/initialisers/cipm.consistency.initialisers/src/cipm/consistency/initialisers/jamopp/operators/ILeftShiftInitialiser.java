package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.LeftShift;

public interface ILeftShiftInitialiser extends IShiftOperatorInitialiser {
	@Override
	public LeftShift instantiate();

}
