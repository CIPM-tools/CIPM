package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.ReflectiveClassReference;

public interface IReflectiveClassReferenceInitialiser extends IReferenceInitialiser {
	@Override
	public ReflectiveClassReference instantiate();

}
