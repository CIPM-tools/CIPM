package cipm.consistency.initialisers.jamopp.imports;

import org.emftext.language.java.imports.PackageImport;

public interface IPackageImportInitialiser extends IImportInitialiser {
	@Override
	public PackageImport instantiate();

}
