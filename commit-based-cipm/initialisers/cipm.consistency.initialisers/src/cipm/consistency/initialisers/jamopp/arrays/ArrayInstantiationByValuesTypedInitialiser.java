package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArrayInstantiationByValuesTyped;
import org.emftext.language.java.arrays.ArraysFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ArrayInstantiationByValuesTypedInitialiser extends AbstractInitialiserBase
		implements IArrayInstantiationByValuesTypedInitialiser {
	@Override
	public IArrayInstantiationByValuesTypedInitialiser newInitialiser() {
		return new ArrayInstantiationByValuesTypedInitialiser();
	}

	@Override
	public ArrayInstantiationByValuesTyped instantiate() {
		return ArraysFactory.eINSTANCE.createArrayInstantiationByValuesTyped();
	}
}