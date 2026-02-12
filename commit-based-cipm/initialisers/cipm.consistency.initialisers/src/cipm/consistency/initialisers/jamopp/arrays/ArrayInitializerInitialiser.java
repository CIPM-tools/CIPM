package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArraysFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.arrays.ArrayInitializer;

public class ArrayInitializerInitialiser extends AbstractInitialiserBase implements IArrayInitializerInitialiser {
	@Override
	public IArrayInitializerInitialiser newInitialiser() {
		return new ArrayInitializerInitialiser();
	}

	@Override
	public ArrayInitializer instantiate() {
		return ArraysFactory.eINSTANCE.createArrayInitializer();
	}
}
