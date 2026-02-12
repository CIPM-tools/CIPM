package cipm.consistency.initialisers.jamopp.parameters;

import org.emftext.language.java.parameters.ParametersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.parameters.OrdinaryParameter;

public class OrdinaryParameterInitialiser extends AbstractInitialiserBase implements IOrdinaryParameterInitialiser {
	@Override
	public OrdinaryParameter instantiate() {
		return ParametersFactory.eINSTANCE.createOrdinaryParameter();
	}

	@Override
	public IOrdinaryParameterInitialiser newInitialiser() {
		return new OrdinaryParameterInitialiser();
	}
}
