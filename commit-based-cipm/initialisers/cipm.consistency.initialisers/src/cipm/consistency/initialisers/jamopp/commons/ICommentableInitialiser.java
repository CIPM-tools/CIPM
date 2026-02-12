package cipm.consistency.initialisers.jamopp.commons;

import org.emftext.language.java.commons.Commentable;

import cipm.consistency.initialisers.jamopp.IJaMoPPEObjectInitialiser;

public interface ICommentableInitialiser extends IJaMoPPEObjectInitialiser {
	@Override
	public Commentable instantiate();

}
