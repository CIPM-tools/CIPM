package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.JumpLabel;

import cipm.consistency.initialisers.jamopp.commons.INamedElementInitialiser;

public interface IJumpLabelInitialiser
		extends INamedElementInitialiser, IStatementInitialiser, IStatementContainerInitialiser {
	@Override
	public JumpLabel instantiate();

}
