package cipm.consistency.initialisers.jamopp.modules;

import org.emftext.language.java.modules.ModulesFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.modules.ExportsModuleDirective;

public class ExportsModuleDirectiveInitialiser extends AbstractInitialiserBase
		implements IExportsModuleDirectiveInitialiser {
	@Override
	public IExportsModuleDirectiveInitialiser newInitialiser() {
		return new ExportsModuleDirectiveInitialiser();
	}

	@Override
	public ExportsModuleDirective instantiate() {
		return ModulesFactory.eINSTANCE.createExportsModuleDirective();
	}
}
