package cipm.consistency.initialisers.jamopp.modules;

import org.emftext.language.java.modules.ModuleDirective;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IModuleDirectiveInitialiser extends ICommentableInitialiser {
	@Override
	public ModuleDirective instantiate();

}
