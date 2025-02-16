package cipm.consistency.initialisers.jamopp.parameters;

import org.emftext.language.java.parameters.Parameter;

import cipm.consistency.initialisers.jamopp.modifiers.IAnnotableAndModifiableInitialiser;
import cipm.consistency.initialisers.jamopp.variables.IVariableInitialiser;

public interface IParameterInitialiser extends IAnnotableAndModifiableInitialiser, IVariableInitialiser {
	@Override
	public Parameter instantiate();
}
