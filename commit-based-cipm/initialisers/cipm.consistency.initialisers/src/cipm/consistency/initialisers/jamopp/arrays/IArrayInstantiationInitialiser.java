package cipm.consistency.initialisers.jamopp.arrays;

import org.emftext.language.java.arrays.ArrayInstantiation;

import cipm.consistency.initialisers.jamopp.references.IReferenceInitialiser;

public interface IArrayInstantiationInitialiser extends IReferenceInitialiser {
	@Override
	public ArrayInstantiation instantiate();
}
