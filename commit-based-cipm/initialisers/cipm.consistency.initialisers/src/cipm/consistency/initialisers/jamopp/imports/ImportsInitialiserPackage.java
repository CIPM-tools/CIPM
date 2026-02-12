package cipm.consistency.initialisers.jamopp.imports;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class ImportsInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this.initCol(new IInitialiser[] { new ClassifierImportInitialiser(), new PackageImportInitialiser(),
				new StaticClassifierImportInitialiser(), new StaticMemberImportInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IClassifierImportInitialiser.class, IImportingElementInitialiser.class,
				IImportInitialiser.class, IPackageImportInitialiser.class, IStaticClassifierImportInitialiser.class,
				IStaticImportInitialiser.class, IStaticMemberImportInitialiser.class, });
	}
}
