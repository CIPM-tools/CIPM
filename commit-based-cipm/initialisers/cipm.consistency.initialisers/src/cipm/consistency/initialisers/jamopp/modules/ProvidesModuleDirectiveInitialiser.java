package cipm.consistency.initialisers.jamopp.modules;

import org.emftext.language.java.modules.ModulesFactory;
import org.emftext.language.java.modules.ProvidesModuleDirective;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ProvidesModuleDirectiveInitialiser extends AbstractInitialiserBase
		implements IProvidesModuleDirectiveInitialiser {
	@Override
	public IProvidesModuleDirectiveInitialiser newInitialiser() {
		return new ProvidesModuleDirectiveInitialiser();
	}

	@Override
	public ProvidesModuleDirective instantiate() {
		return ModulesFactory.eINSTANCE.createProvidesModuleDirective();
	}
}