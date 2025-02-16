package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.ForLoopInitializer;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IForLoopInitializerInitialiser extends ICommentableInitialiser {
	@Override
	public ForLoopInitializer instantiate();

}
