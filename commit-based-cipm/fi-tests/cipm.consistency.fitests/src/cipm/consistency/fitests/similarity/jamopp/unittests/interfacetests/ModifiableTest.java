package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.modifiers.Modifiable;
import org.emftext.language.java.modifiers.Modifier;
import org.emftext.language.java.modifiers.ModifiersPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ModifiableTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Modifier> modifier1 = () -> getAPI().newFinal();
	private final Supplier<Modifier> modifier2 = () -> getAPI().newAbstract();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Modifiable.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testModifier(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithAddedFeat(ModifiersPackage.Literals.MODIFIABLE__MODIFIERS, modifier1.get())
						.createNow(),
				getAPI().newX(cls).xWithAddedFeat(ModifiersPackage.Literals.MODIFIABLE__MODIFIERS, modifier2.get())
						.createNow(),
				ModifiersPackage.Literals.MODIFIABLE__MODIFIERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testModifierSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ModifiersPackage.Literals.MODIFIABLE__MODIFIERS,
								new Modifier[] { modifier1.get(), modifier2.get() })
						.createNow(),
				getAPI().newX(cls).xWithAddedFeat(ModifiersPackage.Literals.MODIFIABLE__MODIFIERS, modifier2.get())
						.createNow(),
				ModifiersPackage.Literals.MODIFIABLE__MODIFIERS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testModifierNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithAddedFeat(ModifiersPackage.Literals.MODIFIABLE__MODIFIERS, modifier1.get()).createNow(),
				ModifiersPackage.Literals.MODIFIABLE__MODIFIERS);
	}
}
