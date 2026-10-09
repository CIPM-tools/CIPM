package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.WhileLoop;

public interface IWhileLoopInitialiser
		extends IConditionalInitialiser, IStatementInitialiser, IStatementContainerInitialiser {
	@Override
	public WhileLoop instantiate();

}
