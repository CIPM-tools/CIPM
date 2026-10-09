package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.OperatorsFactory;
import org.emftext.language.java.operators.PlusPlus;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class PlusPlusInitialiser extends AbstractInitialiserBase implements IPlusPlusInitialiser {
	@Override
	public IPlusPlusInitialiser newInitialiser() {
		return new PlusPlusInitialiser();
	}

	@Override
	public PlusPlus instantiate() {
		return OperatorsFactory.eINSTANCE.createPlusPlus();
	}
}