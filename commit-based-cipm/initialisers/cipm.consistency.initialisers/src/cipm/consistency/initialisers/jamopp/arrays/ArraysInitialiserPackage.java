package cipm.consistency.initialisers.jamopp.arrays;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class ArraysInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new ArrayDimensionInitialiser(), new ArrayInitializerInitialiser(),
				new ArrayInstantiationBySizeInitialiser(), new ArrayInstantiationByValuesTypedInitialiser(),
				new ArrayInstantiationByValuesUntypedInitialiser(), new ArraySelectorInitialiser() });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IArrayDimensionInitialiser.class, IArrayInitializationValueInitialiser.class,
				IArrayInitializerInitialiser.class, IArrayInstantiationBySizeInitialiser.class,
				IArrayInstantiationByValuesInitialiser.class, IArrayInstantiationByValuesTypedInitialiser.class,
				IArrayInstantiationByValuesUntypedInitialiser.class, IArrayInstantiationInitialiser.class,
				IArraySelectorInitialiser.class, IArrayTypeableInitialiser.class });
	}
}
