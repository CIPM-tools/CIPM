package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementContainer;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesStatements;
import cipm.consistency.initialisers.jamopp.statements.IStatementContainerInitialiser;

public class StatementContainerTest extends AbstractJaMoPPSimilarityTest implements UsesStatements {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IStatementContainerInitialiser.class);
	}

	protected StatementContainer initElement(IStatementContainerInitialiser init, Statement st) {
		StatementContainer result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertTrue(init.setStatement(result, st));
		return result;
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testStatement(IStatementContainerInitialiser init, String displayName) {
		var objOne = this.initElement(init, this.createMinimalNullReturn());
		var objTwo = this.initElement(init, this.createMinimalTrivialAssert());

		this.testSimilarity(objOne, objTwo, StatementsPackage.Literals.STATEMENT_CONTAINER__STATEMENT);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testStatementNullCheck(IStatementContainerInitialiser init, String displayName) {
		this.testSimilarityNullCheck(this.initElement(init, this.createMinimalNullReturn()), init, true,
				StatementsPackage.Literals.STATEMENT_CONTAINER__STATEMENT);
	}
}
