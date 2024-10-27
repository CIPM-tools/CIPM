package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.stream.Stream;

import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementListContainer;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesStatements;
import cipm.consistency.initialisers.jamopp.statements.IStatementListContainerInitialiser;

/**
 * {@link StatementListContainer} has no attributes itself.
 * 
 * @author Alp Torac Genc
 */
public class StatementListContainerTest extends AbstractJaMoPPSimilarityTest implements UsesStatements {

	private static Stream<Arguments> provideArguments() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IStatementListContainerInitialiser.class);
	}

	protected StatementListContainer initElement(IStatementListContainerInitialiser init, Statement[] sts) {

		StatementListContainer result = init.instantiate();
		Assertions.assertTrue(init.initialise(result));
		Assertions.assertEquals(init.canContainStatements(result), init.addStatements(result, sts));

		return result;
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testStatements(IStatementListContainerInitialiser init) {
		var objOne = this.initElement(init, new Statement[] { this.createMinimalNullReturn() });
		var objTwo = this.initElement(init, new Statement[] { this.createMinimalTrivialAssert() });

		this.testSimilarity(objOne, objTwo,
				this.getExpectedSimilarityResult(StatementsPackage.Literals.BLOCK__STATEMENTS).booleanValue()
						|| (!init.canContainStatements(objOne) && !init.canContainStatements(objTwo)));
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testStatementsSize(IStatementListContainerInitialiser init) {
		var objOne = this.initElement(init,
				new Statement[] { this.createMinimalTrivialAssert(), this.createMinimalNullReturn() });
		var objTwo = this.initElement(init, new Statement[] { this.createMinimalTrivialAssert() });

		this.testSimilarity(objOne, objTwo,
				this.getExpectedSimilarityResult(StatementsPackage.Literals.BLOCK__STATEMENTS).booleanValue()
						|| (!init.canContainStatements(objOne) && !init.canContainStatements(objTwo)));
	}

	@ParameterizedTest
	@MethodSource("provideArguments")
	public void testStatementsNullCheck(IStatementListContainerInitialiser init) {
		var objOne = this.initElement(init, new Statement[] { this.createMinimalNullReturn() });

		this.testSimilarityNullCheck(objOne, init, true,
				this.getExpectedSimilarityResult(StatementsPackage.Literals.BLOCK__STATEMENTS).booleanValue()
						|| (!init.canContainStatements(objOne)));
	}
}
