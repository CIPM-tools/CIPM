package cipm.consistency.initialisers.jamopp.instantiations;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class InstantiationsInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new ExplicitConstructorCallInitialiser(),
				new NewConstructorCallInitialiser(), new NewConstructorCallWithInferredTypeArgumentsInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IExplicitConstructorCallInitialiser.class, IInitializableInitialiser.class,
				IInstantiationInitialiser.class, INewConstructorCallInitialiser.class,
				INewConstructorCallWithInferredTypeArgumentsInitialiser.class, });
	}
}
