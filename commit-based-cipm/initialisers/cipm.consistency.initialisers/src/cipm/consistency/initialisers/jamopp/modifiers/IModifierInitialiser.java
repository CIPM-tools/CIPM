package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.Modifier;

public interface IModifierInitialiser extends IAnnotationInstanceOrModifierInitialiser {
	@Override
	public Modifier instantiate();

}
