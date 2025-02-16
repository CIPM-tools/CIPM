package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArrayInstantiationByValuesUntyped;
import org.emftext.language.java.arrays.ArraysFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ArrayInstantiationByValuesUntypedInitialiser extends AbstractInitialiserBase
		implements IArrayInstantiationByValuesUntypedInitialiser {
	@Override
	public IArrayInstantiationByValuesUntypedInitialiser newInitialiser() {
		return new ArrayInstantiationByValuesUntypedInitialiser();
	}

	@Override
	public ArrayInstantiationByValuesUntyped instantiate() {
		return ArraysFactory.eINSTANCE.createArrayInstantiationByValuesUntyped();
	}
}