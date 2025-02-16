package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.Complement;

public interface IComplementInitialiser extends IUnaryOperatorInitialiser {
	@Override
	public Complement instantiate();

}
