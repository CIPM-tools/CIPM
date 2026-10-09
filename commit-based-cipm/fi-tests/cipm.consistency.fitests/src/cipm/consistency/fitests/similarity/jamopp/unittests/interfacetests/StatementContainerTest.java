package cipm.consistency.fitests.similarity.jamopp.unittests.interfacetests;

import java.util.function.Supplier;
import java.util.stream.Stream;

import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementContainer;
import org.emftext.language.java.statements.StatementsPackage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.JaMoPPArguments;

public class StatementContainerTest extends AbstractJaMoPPSimilarityTest {
	private final Supplier<Statement> statement1 = () -> getAPI().createNewAssert();
	private final Supplier<Statement> statement2 = () -> getAPI().createNewEmptyStatement();

	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesBySuperAsArgs(StatementContainer.class);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testStatement(Class<?> cls, String displayName) {
		this.testSimilarity(getAPI().newX(cls)
				.xWithFeat(StatementsPackage.Literals.STATEMENT_CONTAINER__STATEMENT, statement1.get()).createNow(),
				getAPI().newX(cls)
						.xWithFeat(StatementsPackage.Literals.STATEMENT_CONTAINER__STATEMENT, statement2.get())
						.createNow(),
				StatementsPackage.Literals.STATEMENT_CONTAINER__STATEMENT);
	}

	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void testStatementNullCheck(Class<?> cls, String displayName) {
		this.testSimilarityNullCheck(getAPI().newX(cls)
				.xWithFeat(StatementsPackage.Literals.STATEMENT_CONTAINER__STATEMENT, statement1.get()).createNow(),
				StatementsPackage.Literals.STATEMENT_CONTAINER__STATEMENT);
	}
}
