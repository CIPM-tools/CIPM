package cipm.consistency.initialisers.jamopp.modifiers;

import org.emftext.language.java.modifiers.Open;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IOpenInitialiser extends ICommentableInitialiser {
	@Override
	public Open instantiate();

}
