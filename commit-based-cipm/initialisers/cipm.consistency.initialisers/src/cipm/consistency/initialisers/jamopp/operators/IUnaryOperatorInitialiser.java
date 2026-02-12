package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.UnaryOperator;

public interface IUnaryOperatorInitialiser extends IOperatorInitialiser {
	@Override
	public UnaryOperator instantiate();

}