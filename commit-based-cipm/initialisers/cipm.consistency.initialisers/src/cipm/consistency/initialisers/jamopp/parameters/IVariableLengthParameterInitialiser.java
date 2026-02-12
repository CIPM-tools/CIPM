package cipm.consistency.initialisers.jamopp.parameters;

import org.emftext.language.java.parameters.VariableLengthParameter;

import cipm.consistency.initialisers.jamopp.annotations.IAnnotableInitialiser;

public interface IVariableLengthParameterInitialiser extends IAnnotableInitialiser, IParameterInitialiser {
	@Override
	public VariableLengthParameter instantiate();
}
