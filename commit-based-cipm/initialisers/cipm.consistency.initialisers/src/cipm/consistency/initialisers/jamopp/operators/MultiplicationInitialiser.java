package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.Multiplication;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class MultiplicationInitialiser extends AbstractInitialiserBase implements IMultiplicationInitialiser {
	@Override
	public IMultiplicationInitialiser newInitialiser() {
		return new MultiplicationInitialiser();
	}

	@Override
	public Multiplication instantiate() {
		return OperatorsFactory.eINSTANCE.createMultiplication();
	}
}