package cipm.consistency.initialisers.jamopp.generics;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class GenericsInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new ExtendsTypeArgumentInitialiser(),
				new QualifiedTypeArgumentInitialiser(), new SuperTypeArgumentInitialiser(),
				new TypeParameterInitialiser(), new UnknownTypeArgumentInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { ICallTypeArgumentableInitialiser.class, IExtendsTypeArgumentInitialiser.class,
				IQualifiedTypeArgumentInitialiser.class, ISuperTypeArgumentInitialiser.class,
				ITypeArgumentableInitialiser.class, ITypeArgumentInitialiser.class, ITypeParameterInitialiser.class,
				ITypeParametrizableInitialiser.class, IUnknownTypeArgumentInitialiser.class, });
	}
}
