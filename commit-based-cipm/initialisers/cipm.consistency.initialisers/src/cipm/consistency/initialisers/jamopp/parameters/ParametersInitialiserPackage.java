package cipm.consistency.initialisers.jamopp.parameters;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class ParametersInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new CatchParameterInitialiser(), new OrdinaryParameterInitialiser(),
				new ReceiverParameterInitialiser(), new VariableLengthParameterInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { ICatchParameterInitialiser.class, IOrdinaryParameterInitialiser.class,
				IParameterInitialiser.class, IParametrizableInitialiser.class, IReceiverParameterInitialiser.class,
				IVariableLengthParameterInitialiser.class, });
	}
}
