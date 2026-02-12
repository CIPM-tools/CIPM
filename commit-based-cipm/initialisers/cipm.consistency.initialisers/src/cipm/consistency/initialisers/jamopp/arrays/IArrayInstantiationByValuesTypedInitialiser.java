package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArrayInstantiationByValuesTyped;

import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface IArrayInstantiationByValuesTypedInitialiser
		extends IArrayInstantiationByValuesInitialiser, ITypedElementInitialiser {
	@Override
	public ArrayInstantiationByValuesTyped instantiate();
}
