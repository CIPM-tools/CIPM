package cipm.consistency.initialisers.jamopp.parameters;

import org.emftext.language.java.parameters.Parameter;
import org.emftext.language.java.parameters.Parametrizable;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IParametrizableInitialiser extends ICommentableInitialiser {
	@Override
	public Parametrizable instantiate();

	public default boolean addParameter(Parametrizable parametrizable, Parameter param) {
		if (param != null) {
			parametrizable.getParameters().add(param);
			return parametrizable.getParameters().contains(param);
		}
		return true;
	}

	public default boolean addParameters(Parametrizable parametrizable, Parameter[] params) {
		return this.doMultipleModifications(parametrizable, params, this::addParameter);
	}
}
