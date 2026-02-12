package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.DecimalFloatLiteral;
import org.emftext.language.java.literals.LiteralsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class DecimalFloatLiteralInitialiser extends AbstractInitialiserBase implements IDecimalFloatLiteralInitialiser {
	@Override
	public IDecimalFloatLiteralInitialiser newInitialiser() {
		return new DecimalFloatLiteralInitialiser();
	}

	@Override
	public DecimalFloatLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createDecimalFloatLiteral();
	}
}