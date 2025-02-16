package cipm.consistency.initialisers.jamopp.modules;

import org.emftext.language.java.modules.UsesModuleDirective;

import cipm.consistency.initialisers.jamopp.types.ITypedElementInitialiser;

public interface IUsesModuleDirectiveInitialiser extends IModuleDirectiveInitialiser, ITypedElementInitialiser {
	@Override
	public UsesModuleDirective instantiate();
}
