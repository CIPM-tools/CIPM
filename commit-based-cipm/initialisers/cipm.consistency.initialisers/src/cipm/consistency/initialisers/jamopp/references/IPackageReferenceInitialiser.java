package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.PackageReference;

import cipm.consistency.initialisers.jamopp.commons.INamespaceAwareElementInitialiser;

public interface IPackageReferenceInitialiser
		extends INamespaceAwareElementInitialiser, IReferenceableElementInitialiser {
	@Override
	public PackageReference instantiate();
}
