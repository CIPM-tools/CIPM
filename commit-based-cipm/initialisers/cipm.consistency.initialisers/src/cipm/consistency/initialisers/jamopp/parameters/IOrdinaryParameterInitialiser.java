package cipm.consistency.initialisers.jamopp.parameters;

import org.emftext.language.java.parameters.OrdinaryParameter;

public interface IOrdinaryParameterInitialiser extends IParameterInitialiser {
	@Override
	public OrdinaryParameter instantiate();
}
