package cipm.consistency.initialisers.jamopp.parameters;

import org.emftext.language.java.parameters.ParametersFactory;
import org.emftext.language.java.parameters.VariableLengthParameter;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class VariableLengthParameterInitialiser extends AbstractInitialiserBase
		implements IVariableLengthParameterInitialiser {
	@Override
	public VariableLengthParameter instantiate() {
		return ParametersFactory.eINSTANCE.createVariableLengthParameter();
	}

	@Override
	public IVariableLengthParameterInitialiser newInitialiser() {
		return new VariableLengthParameterInitialiser();
	}
}
