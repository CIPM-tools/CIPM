package cipm.consistency.initialisers.jamopp.modules;

import org.emftext.language.java.modules.ModulesFactory;
import org.emftext.language.java.modules.OpensModuleDirective;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class OpensModuleDirectiveInitialiser extends AbstractInitialiserBase
		implements IOpensModuleDirectiveInitialiser {
	@Override
	public IOpensModuleDirectiveInitialiser newInitialiser() {
		return new OpensModuleDirectiveInitialiser();
	}

	@Override
	public OpensModuleDirective instantiate() {
		return ModulesFactory.eINSTANCE.createOpensModuleDirective();
	}
}
