package cipm.consistency.initialisers.jamopp.imports;

import org.emftext.language.java.imports.ImportsFactory;
import org.emftext.language.java.imports.PackageImport;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class PackageImportInitialiser extends AbstractInitialiserBase implements IPackageImportInitialiser {
	@Override
	public IPackageImportInitialiser newInitialiser() {
		return new PackageImportInitialiser();
	}

	@Override
	public PackageImport instantiate() {
		return ImportsFactory.eINSTANCE.createPackageImport();
	}
}