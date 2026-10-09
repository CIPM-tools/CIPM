package cipm.consistency.initialisers.jamopp.containers;

import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.containers.Package;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class PackageInitialiser extends AbstractInitialiserBase implements IPackageInitialiser {
	@Override
	public Package instantiate() {
		var fac = ContainersFactory.eINSTANCE;
		return fac.createPackage();
	}

	@Override
	public IPackageInitialiser newInitialiser() {
		return new PackageInitialiser();
	}
}
