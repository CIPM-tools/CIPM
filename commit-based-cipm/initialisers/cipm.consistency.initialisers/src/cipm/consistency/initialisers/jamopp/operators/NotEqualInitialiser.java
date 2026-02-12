package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.NotEqual;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class NotEqualInitialiser extends AbstractInitialiserBase implements INotEqualInitialiser {
	@Override
	public INotEqualInitialiser newInitialiser() {
		return new NotEqualInitialiser();
	}

	@Override
	public NotEqual instantiate() {
		return OperatorsFactory.eINSTANCE.createNotEqual();
	}
}