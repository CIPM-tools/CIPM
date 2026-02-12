package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.DecimalDoubleLiteral;
import org.emftext.language.java.literals.LiteralsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class DecimalDoubleLiteralInitialiser extends AbstractInitialiserBase
		implements IDecimalDoubleLiteralInitialiser {
	@Override
	public IDecimalDoubleLiteralInitialiser newInitialiser() {
		return new DecimalDoubleLiteralInitialiser();
	}

	@Override
	public DecimalDoubleLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createDecimalDoubleLiteral();
	}
}