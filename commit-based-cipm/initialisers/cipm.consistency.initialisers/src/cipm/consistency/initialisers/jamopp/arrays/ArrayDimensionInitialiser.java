package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArraysFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.arrays.ArrayDimension;

public class ArrayDimensionInitialiser extends AbstractInitialiserBase implements IArrayDimensionInitialiser {
	@Override
	public IArrayDimensionInitialiser newInitialiser() {
		return new ArrayDimensionInitialiser();
	}

	@Override
	public ArrayDimension instantiate() {
		return ArraysFactory.eINSTANCE.createArrayDimension();
	}
}