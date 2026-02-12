package cipm.consistency.initialisers.jamopp.modules;

import org.emftext.language.java.modifiers.ModuleRequiresModifier;
import org.emftext.language.java.modules.ModuleReference;
import org.emftext.language.java.modules.RequiresModuleDirective;

public interface IRequiresModuleDirectiveInitialiser extends IModuleDirectiveInitialiser {
	@Override
	public RequiresModuleDirective instantiate();

	public default boolean setModifier(RequiresModuleDirective rmd, ModuleRequiresModifier modif) {
		rmd.setModifier(modif);
		return (modif == null && rmd.getModifier() == null) || rmd.getModifier().equals(modif);
	}

	public default boolean setRequiredModule(RequiresModuleDirective rmd, ModuleReference reqMod) {
		rmd.setRequiredModule(reqMod);
		return (reqMod == null && rmd.getRequiredModule() == null) || rmd.getRequiredModule().equals(reqMod);
	}
}
