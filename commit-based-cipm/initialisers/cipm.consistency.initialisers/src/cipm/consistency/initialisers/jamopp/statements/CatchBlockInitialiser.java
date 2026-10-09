package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.CatchBlock;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class CatchBlockInitialiser extends AbstractInitialiserBase implements ICatchBlockInitialiser {
	@Override
	public CatchBlock instantiate() {
		return StatementsFactory.eINSTANCE.createCatchBlock();
	}

	@Override
	public CatchBlockInitialiser newInitialiser() {
		return new CatchBlockInitialiser();
	}
}