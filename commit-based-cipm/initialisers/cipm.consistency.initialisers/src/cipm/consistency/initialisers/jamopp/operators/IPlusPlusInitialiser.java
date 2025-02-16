package cipm.consistency.initialisers.jamopp.operators;

import org.emftext.language.java.operators.PlusPlus;

public interface IPlusPlusInitialiser extends IUnaryModificationOperatorInitialiser {
	@Override
	public PlusPlus instantiate();

}
