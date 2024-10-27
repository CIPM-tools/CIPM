package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.generics.GenericsPackage;
import org.emftext.language.java.generics.TypeArgument;
import org.emftext.language.java.generics.TypeArgumentable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeArguments;
import cipm.consistency.initialisers.jamopp.generics.ITypeArgumentableInitialiser;

public class TypeArgumentableTest extends AbstractJaMoPPSimilarityTest implements UsesTypeArguments {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getEachInitialiserArgumentsOnceFor(ITypeArgumentableInitialiser.class);
	}

	protected TypeArgumentable initElement(ITypeArgumentableInitialiser init, TypeArgument[] typeArgs) {
		TypeArgumentable result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addTypeArguments(result, typeArgs));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testTypeArgument(ITypeArgumentableInitialiser init) {
		var objOne = this.initElement(init, new TypeArgument[] { this.createMinimalExtendsTAWithCls("cls1") });
		var objTwo = this.initElement(init, new TypeArgument[] { this.createMinimalSuperTAWithCls("cls2") });

		this.testSimilarity(objOne, objTwo, GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testTypeArgumentSize(ITypeArgumentableInitialiser init) {
		var objOne = this.initElement(init, new TypeArgument[] { this.createMinimalExtendsTAWithCls("cls1"),
				this.createMinimalSuperTAWithCls("cls2") });
		var objTwo = this.initElement(init, new TypeArgument[] { this.createMinimalExtendsTAWithCls("cls1") });

		this.testSimilarity(objOne, objTwo, GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testTypeArgumentNullCheck(ITypeArgumentableInitialiser init) {
		this.testSimilarityNullCheck(
				this.initElement(init, new TypeArgument[] { this.createMinimalExtendsTAWithCls("cls1") }), init, true,
				GenericsPackage.Literals.TYPE_ARGUMENTABLE__TYPE_ARGUMENTS);
	}
}
