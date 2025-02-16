package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.Transient;

public interface ITransientInitialiser extends IModifierInitialiser {
	@Override
	public Transient instantiate();

}
