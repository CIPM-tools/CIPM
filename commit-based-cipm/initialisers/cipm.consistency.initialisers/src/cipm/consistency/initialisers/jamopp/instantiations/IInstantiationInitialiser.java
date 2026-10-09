package cipm.consistency.initialisers.jamopp.instantiations;

import org.emftext.language.java.instantiations.Instantiation;

import cipm.consistency.initialisers.jamopp.generics.ICallTypeArgumentableInitialiser;
import cipm.consistency.initialisers.jamopp.references.IArgumentableInitialiser;
import cipm.consistency.initialisers.jamopp.references.IReferenceInitialiser;

public interface IInstantiationInitialiser
		extends IArgumentableInitialiser, IReferenceInitialiser, ICallTypeArgumentableInitialiser {
	@Override
	public Instantiation instantiate();
}
