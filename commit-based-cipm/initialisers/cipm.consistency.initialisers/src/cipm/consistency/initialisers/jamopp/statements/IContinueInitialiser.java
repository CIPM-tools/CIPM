package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.Continue;

public interface IContinueInitialiser extends IJumpInitialiser {
	@Override
	public Continue instantiate();

}
