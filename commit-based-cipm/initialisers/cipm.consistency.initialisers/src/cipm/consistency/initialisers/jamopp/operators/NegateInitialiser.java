package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.Negate;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class NegateInitialiser extends AbstractInitialiserBase implements INegateInitialiser {
	@Override
	public INegateInitialiser newInitialiser() {
		return new NegateInitialiser();
	}

	@Override
	public Negate instantiate() {
		return OperatorsFactory.eINSTANCE.createNegate();
	}
}