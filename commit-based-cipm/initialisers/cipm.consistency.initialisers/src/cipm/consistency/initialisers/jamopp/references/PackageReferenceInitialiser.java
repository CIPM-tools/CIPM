package cipm.consistency.initialisers.jamopp.references;

import org.emftext.language.java.references.PackageReference;
import org.emftext.language.java.references.ReferencesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class PackageReferenceInitialiser extends AbstractInitialiserBase implements IPackageReferenceInitialiser {
	@Override
	public IPackageReferenceInitialiser newInitialiser() {
		return new PackageReferenceInitialiser();
	}

	@Override
	public PackageReference instantiate() {
		return ReferencesFactory.eINSTANCE.createPackageReference();
	}
}