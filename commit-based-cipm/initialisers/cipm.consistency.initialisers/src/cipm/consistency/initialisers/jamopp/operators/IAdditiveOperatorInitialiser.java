package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.AdditiveOperator;

public interface IAdditiveOperatorInitialiser extends IOperatorInitialiser {
	@Override
	public AdditiveOperator instantiate();

}
