package cipm.consistency.initialisers.jamopp.containers;

import org.emftext.language.java.containers.Module;
import org.emftext.language.java.containers.Package;
import org.emftext.language.java.modifiers.Open;
import org.emftext.language.java.modules.ModuleDirective;

public interface IModuleInitialiser extends IJavaRootInitialiser {
	@Override
	public Module instantiate();

	public default boolean setOpen(Module mod, Open open) {
		mod.setOpen(open);
		return (open == null && mod.getOpen() == null) || mod.getOpen().equals(open);
	}

	public default boolean addTarget(Module mod, ModuleDirective target) {
		if (target != null) {
			mod.getTarget().add(target);
			return mod.getTarget().contains(target);
		}
		return true;
	}

	public default boolean addTargets(Module mod, ModuleDirective[] targets) {
		return this.doMultipleModifications(mod, targets, this::addTarget);
	}

	public default boolean addPackage(Module mod, Package pac) {
		if (pac != null) {
			mod.getPackages().add(pac);
			return mod.getPackages().contains(pac);
		}
		return true;
	}

	public default boolean addPackages(Module mod, Package[] pacs) {
		return this.doMultipleModifications(mod, pacs, this::addPackage);
	}
}
