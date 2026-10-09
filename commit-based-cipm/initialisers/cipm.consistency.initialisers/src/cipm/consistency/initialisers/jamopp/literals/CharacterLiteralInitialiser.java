package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.CharacterLiteral;
import org.emftext.language.java.literals.LiteralsFactory;

import cipm.consistency.initialisers.AbstractInitialiserBase;

public class CharacterLiteralInitialiser extends AbstractInitialiserBase implements ICharacterLiteralInitialiser {
	@Override
	public ICharacterLiteralInitialiser newInitialiser() {
		return new CharacterLiteralInitialiser();
	}

	@Override
	public CharacterLiteral instantiate() {
		return LiteralsFactory.eINSTANCE.createCharacterLiteral();
	}
}