package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.members.ExceptionThrower;
import org.emftext.language.java.members.MembersPackage;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesTypeReferences;
import cipm.consistency.initialisers.jamopp.members.IExceptionThrowerInitialiser;

public class ExceptionThrowerTest extends AbstractJaMoPPSimilarityTest implements UsesTypeReferences {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getEachInitialiserArgumentsOnceFor(IExceptionThrowerInitialiser.class);
	}

	protected ExceptionThrower initElement(IExceptionThrowerInitialiser init,
			NamespaceClassifierReference[] exceptions) {
		ExceptionThrower result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.addExceptions(result, exceptions));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testExceptions(IExceptionThrowerInitialiser init) {
		var objOne = this.initElement(init, new NamespaceClassifierReference[] { this.createMinimalCNR("cls1") });
		var objTwo = this.initElement(init, new NamespaceClassifierReference[] { this.createMinimalCNR("cls2") });

		this.testSimilarity(objOne, objTwo, MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testExceptionsSize(IExceptionThrowerInitialiser init) {
		var objOne = this.initElement(init,
				new NamespaceClassifierReference[] { this.createMinimalCNR("cls1"), this.createMinimalCNR("cls2") });
		var objTwo = this.initElement(init, new NamespaceClassifierReference[] { this.createMinimalCNR("cls1") });

		this.testSimilarity(objOne, objTwo, MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS);
	}

	@Disabled("Disabled till null pointer exceptions are fixed")
	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testExceptionsNullCheck(IExceptionThrowerInitialiser init) {
		this.testSimilarityNullCheck(
				this.initElement(init, new NamespaceClassifierReference[] { this.createMinimalCNR("cls1") }), init,
				true, MembersPackage.Literals.EXCEPTION_THROWER__EXCEPTIONS);
	}
}
