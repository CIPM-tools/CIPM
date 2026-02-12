package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.Self;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface ISelfInitialiser extends ICommentableInitialiser {
	@Override
	public Self instantiate();

}
