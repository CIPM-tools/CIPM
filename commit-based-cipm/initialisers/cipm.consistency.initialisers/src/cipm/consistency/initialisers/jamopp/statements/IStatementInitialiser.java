package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.Statement;

import cipm.consistency.initialisers.jamopp.commons.ICommentableInitialiser;

public interface IStatementInitialiser extends ICommentableInitialiser {
	@Override
	public Statement instantiate();
}