package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.modifiers.Modifiable;
import org.emftext.language.java.modifiers.Modifier;
import org.emftext.language.java.modifiers.ModifiersPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesModifiers;
import cipm.consistency.initialisers.jamopp.modifiers.IModifiableInitialiser;

public class ModifiableTest extends AbstractJaMoPPSimilarityTest implements UsesModifiers {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IModifiableInitialiser.class);
	}

	protected Modifiable initElement(IModifiableInitialiser init, Modifier[] modifs) {
		Modifiable result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addModifiers(result, modifs));
		return result;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testModifier(IModifiableInitialiser init, String displayName) {
		var objOne = this.initElement(init, new Modifier[] { this.createFinal() });
		var objTwo = this.initElement(init, new Modifier[] { this.createAbstract() });

		this.testSimilarity(objOne, objTwo, ModifiersPackage.Literals.MODIFIABLE__MODIFIERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testModifierSize(IModifiableInitialiser init, String displayName) {
		var objOne = this.initElement(init, new Modifier[] { this.createFinal(), this.createAbstract() });
		var objTwo = this.initElement(init, new Modifier[] { this.createFinal() });

		this.testSimilarity(objOne, objTwo, ModifiersPackage.Literals.MODIFIABLE__MODIFIERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testModifierNullCheck(IModifiableInitialiser init, String displayName) {
		var objOne = this.initElement(init, new Modifier[] { this.createFinal() });
		var objTwo = init.instantiate();
		Assertions.assertTrue(init.initialise(objTwo));

		this.testSimilarity(objOne, objTwo, ModifiersPackage.Literals.MODIFIABLE__MODIFIERS);
	}
}
