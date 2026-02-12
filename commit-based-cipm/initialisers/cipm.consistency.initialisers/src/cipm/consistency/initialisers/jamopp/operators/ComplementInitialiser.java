package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.Complement;
import org.emftext.language.java.operators.OperatorsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ComplementInitialiser extends AbstractInitialiserBase implements IComplementInitialiser {
	@Override
	public IComplementInitialiser newInitialiser() {
		return new ComplementInitialiser();
	}

	@Override
	public Complement instantiate() {
		return OperatorsFactory.eINSTANCE.createComplement();
	}
}