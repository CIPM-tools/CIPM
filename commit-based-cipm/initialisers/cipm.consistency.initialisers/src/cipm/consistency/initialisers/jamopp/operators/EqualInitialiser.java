package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.Equal;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class EqualInitialiser extends AbstractInitialiserBase implements IEqualInitialiser {
	@Override
	public IEqualInitialiser newInitialiser() {
		return new EqualInitialiser();
	}

	@Override
	public Equal instantiate() {
		return OperatorsFactory.eINSTANCE.createEqual();
	}
}