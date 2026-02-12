package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.AnnotationInstanceOrModifier;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IAnnotationInstanceOrModifierInitialiser extends ICommentableInitialiser {
	@Override
	public AnnotationInstanceOrModifier instantiate();

}
