package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.HexDoubleLiteral;
import org.emftext.language.java.literals.LiteralsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class HexDoubleLiteralInitialiser extends AbstractInitialiserBase implements IHexDoubleLiteralInitialiser {
	@Override
	public IHexDoubleLiteralInitialiser newInitialiser() {
		return new HexDoubleLiteralInitialiser();
	}

	@Override
	public HexDoubleLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createHexDoubleLiteral();
	}
}