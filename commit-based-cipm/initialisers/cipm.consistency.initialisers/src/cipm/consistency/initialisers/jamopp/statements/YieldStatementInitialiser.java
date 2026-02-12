package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.StatementsFactory;
import org.emftext.language.java.statements.YieldStatement;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class YieldStatementInitialiser extends AbstractInitialiserBase implements IYieldStatementInitialiser {
	@Override
	public IYieldStatementInitialiser newInitialiser() {
		return new YieldStatementInitialiser();
	}

	@Override
	public YieldStatement instantiate() {
		return StatementsFactory.eINSTANCE.createYieldStatement();
	}
}