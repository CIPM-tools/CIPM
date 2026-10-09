package cipm.consistency.initialisers.jamopp.statements;

import org.emftext.language.java.statements.DoWhileLoop;
import org.emftext.language.java.statements.StatementsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class DoWhileLoopInitialiser extends AbstractInitialiserBase implements IDoWhileLoopInitialiser {
	@Override
	public IDoWhileLoopInitialiser newInitialiser() {
		return new DoWhileLoopInitialiser();
	}

	@Override
	public DoWhileLoop instantiate() {
		return StatementsFactory.eINSTANCE.createDoWhileLoop();
	}
}