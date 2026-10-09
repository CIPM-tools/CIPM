package cipm.consistency.initialisers.jamopp.modules;

import java.util.Collection;

import cipm.consistency.initialisers.IInitialiser;
import cipm.consistency.initialisers.IInitialiserPackage;

public class ModulesInitialiserPackage implements IInitialiserPackage {
	@Override
	public Collection<IInitialiser> getInitialiserInstances() {
		return this
				.initCol(new IInitialiser[] { new ExportsModuleDirectiveInitialiser(), new ModuleReferenceInitialiser(),
						new OpensModuleDirectiveInitialiser(), new ProvidesModuleDirectiveInitialiser(),
						new RequiresModuleDirectiveInitialiser(), new UsesModuleDirectiveInitialiser(), });
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<Class<? extends IInitialiser>> getInitialiserInterfaceTypes() {
		return this.initCol(new Class[] { IAccessProvidingModuleDirectiveInitialiser.class,
				IExportsModuleDirectiveInitialiser.class, IModuleDirectiveInitialiser.class,
				IModuleReferenceInitialiser.class, IOpensModuleDirectiveInitialiser.class,
				IProvidesModuleDirectiveInitialiser.class, IRequiresModuleDirectiveInitialiser.class,
				IUsesModuleDirectiveInitialiser.class, });
	}
}
