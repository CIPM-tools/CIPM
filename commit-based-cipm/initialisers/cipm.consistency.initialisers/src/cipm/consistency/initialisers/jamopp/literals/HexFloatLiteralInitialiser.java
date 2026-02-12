package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.HexFloatLiteral;
import org.emftext.language.java.literals.LiteralsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class HexFloatLiteralInitialiser extends AbstractInitialiserBase implements IHexFloatLiteralInitialiser {
	@Override
	public IHexFloatLiteralInitialiser newInitialiser() {
		return new HexFloatLiteralInitialiser();
	}

	@Override
	public HexFloatLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createHexFloatLiteral();
	}
}