package cipm.consistency.initialisers.jamopp.containers;

import org.emftext.language.java.containers.ContainersFactory;
import org.emftext.language.java.containers.Module;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ModuleInitialiser extends AbstractInitialiserBase implements IModuleInitialiser {
	@Override
	public Module instantiate() {
		var fac = ContainersFactory.eINSTANCE;
		return fac.createModule();
	}

	@Override
	public IModuleInitialiser newInitialiser() {
		return new ModuleInitialiser();
	}
}
