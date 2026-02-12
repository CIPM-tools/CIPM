package cipm.consistency.initialisers.jamopp.parameters;

import org.emftext.language.java.parameters.ParametersFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.parameters.CatchParameter;

public class CatchParameterInitialiser extends AbstractInitialiserBase implements ICatchParameterInitialiser {
	@Override
	public CatchParameter instantiate() {
		return ParametersFactory.eINSTANCE.createCatchParameter();
	}

	@Override
	public ICatchParameterInitialiser newInitialiser() {
		return new CatchParameterInitialiser();
	}
}
