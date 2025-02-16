package cipm.consistency.initialisers.jamopp.modules;

import org.emftext.language.java.containers.Package;
import org.emftext.language.java.modules.AccessProvidingModuleDirective;
import org.emftext.language.java.modules.ModuleReference;

import cipm.consistency.initialisers.jamopp.commons.INamespaceAwareElementInitialiser;

public interface IAccessProvidingModuleDirectiveInitialiser
		extends IModuleDirectiveInitialiser, INamespaceAwareElementInitialiser {
	@Override
	public AccessProvidingModuleDirective instantiate();

	public default boolean setAccessablePackage(AccessProvidingModuleDirective apmd, Package accessablePac) {
		apmd.setAccessablePackage(accessablePac);
		return (accessablePac == null && apmd.getAccessablePackage() == null)
				|| apmd.getAccessablePackage().equals(accessablePac);
	}

	public default boolean addModule(AccessProvidingModuleDirective apmd, ModuleReference mod) {
		if (mod != null) {
			apmd.getModules().add(mod);
			return apmd.getModules().contains(mod);
		}
		return true;
	}

	public default boolean addModules(AccessProvidingModuleDirective apmd, ModuleReference[] mods) {
		return this.doMultipleModifications(apmd, mods, this::addModule);
	}
}
