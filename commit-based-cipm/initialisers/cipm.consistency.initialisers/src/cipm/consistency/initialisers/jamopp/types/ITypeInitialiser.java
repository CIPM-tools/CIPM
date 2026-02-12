package cipm.consistency.initialisers.jamopp.types;

import org.emftext.language.java.types.Type;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface ITypeInitialiser extends ICommentableInitialiser {
	@Override
	public Type instantiate();
}
