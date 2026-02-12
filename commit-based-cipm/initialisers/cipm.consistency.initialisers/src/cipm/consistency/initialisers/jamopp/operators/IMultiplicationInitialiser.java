package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.Multiplication;

public interface IMultiplicationInitialiser extends IMultiplicativeOperatorInitialiser {
	@Override
	public Multiplication instantiate();

}
