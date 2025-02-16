package cipm.consistency.initialisers.jamopp.parameters;

import org.emftext.language.java.parameters.ParametersFactory;
import org.emftext.language.java.parameters.ReceiverParameter;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ReceiverParameterInitialiser extends AbstractInitialiserBase implements IReceiverParameterInitialiser {
	@Override
	public ReceiverParameter instantiate() {
		return ParametersFactory.eINSTANCE.createReceiverParameter();
	}

	@Override
	public IReceiverParameterInitialiser newInitialiser() {
		return new ReceiverParameterInitialiser();
	}
}
