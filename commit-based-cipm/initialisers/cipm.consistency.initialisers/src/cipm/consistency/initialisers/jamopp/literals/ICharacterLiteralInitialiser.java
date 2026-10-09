package cipm.consistency.initialisers.jamopp.literals;

import org.emftext.language.java.literals.CharacterLiteral;

public interface ICharacterLiteralInitialiser extends ILiteralInitialiser {
	@Override
	public CharacterLiteral instantiate();

	public default boolean setValue(CharacterLiteral cl, String val) {
		cl.setValue(val);
		return (val == null && cl.getValue() == null) || cl.getValue().equals(val);
	}
}
