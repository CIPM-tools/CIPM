package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArraySelector;
import org.emftext.language.java.arrays.ArraysFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ArraySelectorInitialiser extends AbstractInitialiserBase implements IArraySelectorInitialiser {
	@Override
	public IArraySelectorInitialiser newInitialiser() {
		return new ArraySelectorInitialiser();
	}

	@Override
	public ArraySelector instantiate() {
		return ArraysFactory.eINSTANCE.createArraySelector();
	}
}