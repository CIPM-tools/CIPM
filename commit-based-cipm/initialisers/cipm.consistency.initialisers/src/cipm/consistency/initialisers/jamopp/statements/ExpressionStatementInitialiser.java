package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.ExpressionStatement;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class ExpressionStatementInitialiser extends AbstractInitialiserBase implements IExpressionStatementInitialiser {
	@Override
	public IExpressionStatementInitialiser newInitialiser() {
		return new ExpressionStatementInitialiser();
	}

	@Override
	public ExpressionStatement instantiate() {
		return StatementsFactory.eINSTANCE.createExpressionStatement();
	}
}