package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.statements.Jump;
import org.emftext.language.java.statements.JumpLabel;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesStatements;
import cipm.consistency.initialisers.jamopp.statements.IJumpInitialiser;

public class JumpTest extends AbstractJaMoPPSimilarityTest implements UsesStatements {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IJumpInitialiser.class);
	}

	protected Jump initElement(IJumpInitialiser init, JumpLabel jl) {
		Jump result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.setTarget(result, jl));
		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testTarget(IJumpInitialiser init) {
		var objOne = this.initElement(init, this.createMinimalJLToNullReturn("jl1"));
		var objTwo = this.initElement(init, this.createMinimalJLToTrivialAssert("jl2"));

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.JUMP__TARGET);
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testTargetNullCheck(IJumpInitialiser init) {
		this.testSimilarityNullCheck(this.initElement(init, this.createMinimalJLToNullReturn("jl1")), init, true,
				StatementsPackage.Literals.JUMP__TARGET);
	}
}
