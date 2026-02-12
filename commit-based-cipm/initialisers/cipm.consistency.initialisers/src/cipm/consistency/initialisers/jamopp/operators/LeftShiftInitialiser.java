package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.LeftShift;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class LeftShiftInitialiser extends AbstractInitialiserBase implements ILeftShiftInitialiser {
	@Override
	public ILeftShiftInitialiser newInitialiser() {
		return new LeftShiftInitialiser();
	}

	@Override
	public LeftShift instantiate() {
		return OperatorsFactory.eINSTANCE.createLeftShift();
	}
}