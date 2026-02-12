package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.Synchronized;

public interface ISynchronizedInitialiser extends IModifierInitialiser {
	@Override
	public Synchronized instantiate();

}
