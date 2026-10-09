package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.Native;

public interface INativeInitialiser extends IModifierInitialiser {
	@Override
	public Native instantiate();

}
