package cipm.consistency.fitests.similarity.jamopp.unittests.impltests;

import org.emftext.language.java.literals.CharacterLiteral;
import org.emftext.language.java.literals.LiteralsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.initialisers.jamopp.literals.CharacterLiteralInitialiser;

public class CharacterLiteralTest extends AbstractJaMoPPSimilarityTest {
	protected CharacterLiteral initElement(String val) {
		var init = new CharacterLiteralInitialiser();
		var lit = init.instantiate();
		Assertions.assertTrue(init.setValue(lit, val));
		return lit;
	}

	@Test
	public void testValue() {
		this.testSimilarity(this.initElement("a"), this.initElement("b"),
				LiteralsPackage.Literals.CHARACTER_LITERAL__VALUE);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@Test
	public void testValueNullCheck() {
		this.testSimilarityNullCheck(this.initElement("a"), new CharacterLiteralInitialiser(), false,
				LiteralsPackage.Literals.CHARACTER_LITERAL__VALUE);
	}
}