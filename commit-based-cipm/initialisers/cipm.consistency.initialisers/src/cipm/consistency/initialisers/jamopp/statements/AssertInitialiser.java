package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

import org.emftext.language.java.statements.Assert;

public class AssertInitialiser extends AbstractInitialiserBase implements IAssertInitialiser {
	@Override
	public IAssertInitialiser newInitialiser() {
		return new AssertInitialiser();
	}

	@Override
	public Assert instantiate() {
		return StatementsFactory.eINSTANCE.createAssert();
	}
}