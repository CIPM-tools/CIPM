package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.HexLongLiteral;
import org.emftext.language.java.literals.LiteralsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class HexLongLiteralInitialiser extends AbstractInitialiserBase implements IHexLongLiteralInitialiser {
	@Override
	public IHexLongLiteralInitialiser newInitialiser() {
		return new HexLongLiteralInitialiser();
	}

	@Override
	public HexLongLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createHexLongLiteral();
	}
}