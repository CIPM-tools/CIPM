package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.Break;

public interface IBreakInitialiser extends IJumpInitialiser {
	@Override
	public Break instantiate();

}
