package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.GreaterThanOrEqual;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class GreaterThanOrEqualInitialiser extends AbstractInitialiserBase implements IGreaterThanOrEqualInitialiser {
	@Override
	public IGreaterThanOrEqualInitialiser newInitialiser() {
		return new GreaterThanOrEqualInitialiser();
	}

	@Override
	public GreaterThanOrEqual instantiate() {
		return OperatorsFactory.eINSTANCE.createGreaterThanOrEqual();
	}
}