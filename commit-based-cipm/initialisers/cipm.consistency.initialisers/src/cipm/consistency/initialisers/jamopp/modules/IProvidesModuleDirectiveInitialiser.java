package cipm.consistency.initialisers.jamopp.modules;

import org.emftext.language.java.modules.ProvidesModuleDirective;
import org.emftext.language.java.types.TypeReference;

import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface IProvidesModuleDirectiveInitialiser extends IModuleDirectiveInitialiser, ITypedElementInitialiser {
	@Override
	public ProvidesModuleDirective instantiate();

	public default boolean addServiceProvider(ProvidesModuleDirective pmd, TypeReference serviceProvider) {
		if (serviceProvider != null) {
			pmd.getServiceProviders().add(serviceProvider);
			return pmd.getServiceProviders().contains(serviceProvider);
		}
		return true;
	}

	public default boolean addServiceProviders(ProvidesModuleDirective pmd, TypeReference[] serviceProviders) {
		return this.doMultipleModifications(pmd, serviceProviders, this::addServiceProvider);
	}
}
