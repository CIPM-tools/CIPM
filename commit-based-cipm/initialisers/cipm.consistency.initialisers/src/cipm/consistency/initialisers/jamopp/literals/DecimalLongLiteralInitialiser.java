package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.DecimalLongLiteral;
import org.emftext.language.java.literals.LiteralsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class DecimalLongLiteralInitialiser extends AbstractInitialiserBase implements IDecimalLongLiteralInitialiser {
	@Override
	public IDecimalLongLiteralInitialiser newInitialiser() {
		return new DecimalLongLiteralInitialiser();
	}

	@Override
	public DecimalLongLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createDecimalLongLiteral();
	}
}