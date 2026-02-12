package cipm.consistency.initialisers.jamopp.modules;

import org.emftext.language.java.modules.ExportsModuleDirective;

public interface IExportsModuleDirectiveInitialiser extends IAccessProvidingModuleDirectiveInitialiser {
	@Override
	public ExportsModuleDirective instantiate();
}
