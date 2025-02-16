package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.DecimalIntegerLiteral;
import org.emftext.language.java.literals.LiteralsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class DecimalIntegerLiteralInitialiser extends AbstractInitialiserBase
		implements IDecimalIntegerLiteralInitialiser {
	@Override
	public IDecimalIntegerLiteralInitialiser newInitialiser() {
		return new DecimalIntegerLiteralInitialiser();
	}

	@Override
	public DecimalIntegerLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createDecimalIntegerLiteral();
	}
}