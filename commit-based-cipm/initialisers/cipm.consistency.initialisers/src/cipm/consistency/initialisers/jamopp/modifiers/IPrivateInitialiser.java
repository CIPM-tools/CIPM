package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.Private;

public interface IPrivateInitialiser extends IModifierInitialiser {
	@Override
	public Private instantiate();

}
