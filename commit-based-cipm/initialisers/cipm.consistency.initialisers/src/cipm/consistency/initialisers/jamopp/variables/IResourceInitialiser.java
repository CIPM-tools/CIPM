package cipm.consistency.initialisers.jamopp.variables;

import org.emftext.language.java.variables.Resource;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IResourceInitialiser extends ICommentableInitialiser {
	@Override
	public Resource instantiate();

}
