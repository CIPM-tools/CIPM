package cipm.consistency.initialisers.jamopp.modules;

import org.emftext.language.java.modules.ModulesFactory;
import org.emftext.language.java.modules.RequiresModuleDirective;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class RequiresModuleDirectiveInitialiser extends AbstractInitialiserBase
		implements IRequiresModuleDirectiveInitialiser {
	@Override
	public IRequiresModuleDirectiveInitialiser newInitialiser() {
		return new RequiresModuleDirectiveInitialiser();
	}

	@Override
	public RequiresModuleDirective instantiate() {
		return ModulesFactory.eINSTANCE.createRequiresModuleDirective();
	}
}