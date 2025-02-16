package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.Static;

public interface IStaticInitialiser extends IModifierInitialiser, IModuleRequiresModifierInitialiser {
	@Override
	public Static instantiate();
}
