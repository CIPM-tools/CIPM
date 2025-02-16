package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.OperatorsFactory;
import org.emftext.language.java.operators.Subtraction;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class SubtractionInitialiser extends AbstractInitialiserBase implements ISubtractionInitialiser {
	@Override
	public ISubtractionInitialiser newInitialiser() {
		return new SubtractionInitialiser();
	}

	@Override
	public Subtraction instantiate() {
		return OperatorsFactory.eINSTANCE.createSubtraction();
	}
}