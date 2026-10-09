package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.Remainder;

public interface IRemainderInitialiser extends IMultiplicativeOperatorInitialiser {
	@Override
	public Remainder instantiate();

}
