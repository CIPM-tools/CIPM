package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.arrays.ArraySelector;
import org.emftext.language.java.references.Reference;
import org.emftext.language.java.references.ReferencesPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class ReferenceTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Reference> next1 = () -> getAPI().createNewSelfReference();
	private final Supplier<Reference> next2 = () -> getAPI().createNewStringReference();

	private final Supplier<ArraySelector> arraySelectors1 = () -> getAPI().newArraySelector()
			.withPosition(getAPI().newDecimalIntegerLiteral(1)).createNow();
	private final Supplier<ArraySelector> arraySelectors2 = () -> getAPI().newArraySelector()
			.withPosition(getAPI().newDecimalIntegerLiteral(2)).createNow();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(Reference.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testNext(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls).xWithFeat(ReferencesPackage.Literals.REFERENCE__NEXT, next1.get()).createNow(),
				getAPI().newX(cls).xWithFeat(ReferencesPackage.Literals.REFERENCE__NEXT, next2.get()).createNow(),
				ReferencesPackage.Literals.REFERENCE__NEXT);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testNextNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(
				getAPI().newX(cls).xWithFeat(ReferencesPackage.Literals.REFERENCE__NEXT, next1.get()).createNow(),
				ReferencesPackage.Literals.REFERENCE__NEXT);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArraySelector(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS, arraySelectors1.get())
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS, arraySelectors2.get())
						.createNow(),
				ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArraySelectorSize(Class<?> cls, String displayName) {
		this.testSimilarity(
				getAPI().newX(cls)
						.xWithAddedFeat(ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS,
								new ArraySelector[] { arraySelectors1.get(), arraySelectors2.get() })
						.createNow(),
				getAPI().newX(cls)
						.xWithAddedFeat(ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS, arraySelectors1.get())
						.createNow(),
				ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testArraySelectorNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithAddedFeat(ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS, arraySelectors1.get())
				.createNow(), ReferencesPackage.Literals.REFERENCE__ARRAY_SELECTORS);
	}
}
