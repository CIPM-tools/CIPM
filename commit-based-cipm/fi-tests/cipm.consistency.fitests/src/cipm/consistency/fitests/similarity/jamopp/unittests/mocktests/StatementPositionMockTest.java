package cipm.consistency.fitests.similarity.jamopp.unittests.mocktests;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.UniqueEList;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.statements.LocalVariableStatement;
import org.emftext.language.java.statements.Statement;
import org.emftext.language.java.statements.StatementListContainer;
import org.emftext.language.java.variables.LocalVariable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.IStatementPositionTest;
import cipm.consistency.fitests.similarity.jamopp.unittests.UsesStatements;

/**
 * Contains tests that check whether the necessary null checks are present in
 * cases, where the focus lies on relative statement positions
 * (preceding/proceeding statements). <br>
 * <br>
 * Although the scenarios described here may be currently unreachable, testing
 * for them ensures that changes to the order, in which preceding/proceeding
 * statements are checked, do not introduce errors to the similarity checking
 * process.
 * 
 * @author Alp Torac Genc
 */
public class StatementPositionMockTest extends AbstractJaMoPPSimilarityTest
		implements UsesStatements, IStatementPositionTest, IMockTest {
	/**
	 * @return Parameters for the test methods in this test class. Refer to their
	 *         documentation for more information.
	 */
	private static Stream<Arguments> genTestParams() {
		var res = new ArrayList<Arguments>();

		for (var stCls : IMockTest.getAllClasses().stream().filter((cls) -> Statement.class.isAssignableFrom(cls))
				.toArray(Class<?>[]::new)) {
			for (var slcCls : IMockTest.getAllClasses().stream()
					.filter((cls) -> StatementListContainer.class.isAssignableFrom(cls)).toArray(Class<?>[]::new)) {
				res.add(Arguments.of(stCls, slcCls,
						String.format("%s inside %s", stCls.getSimpleName(), slcCls.getSimpleName())));
			}
		}

		return res.stream();
	}

	/**
	 * @return A mock of {@link LocalVariableStatement} in form of an instance,
	 *         whose container is the given one and whose local variable is the
	 *         given one.
	 * 
	 * @see {@link #mockEObject(Class)}
	 * @see {@link #mockEObjectWithContainer(Class, EObject)}
	 */
	private LocalVariableStatement mockLVS(LocalVariable lVar, EObject container) {
		var mockLVS = this.mockEObjectWithContainer(LocalVariableStatement.class, container);
		when(mockLVS.getVariable()).thenReturn(lVar);
		return mockLVS;
	}

	/**
	 * Tests similarity checking of {@link Statement} and
	 * {@link StatementListContainer} sub-types for the case, where
	 * {@code statementListContainer.getStatements()} returns null after certain
	 * amounts of calls. <br>
	 * <br>
	 * Runs {@link #testBody(Class, Class, int, int)} for each combination (i, j);
	 * where i,j = {0, ..., N} are the int parameters, for each combination of
	 * statement-statementListContainer sub-types. N will be dynamically decided,
	 * such that N is the smallest number, for which the comparison of statement
	 * instances resembles an ordinary comparison, where {@code .getStatements()}
	 * functions as expected. <br>
	 * The aim of this test method is to ensure the robustness of similarity
	 * checking regarding statement positions (preceding/proceeding statements). In
	 * particular, this test covers some branches that are currently unreachable,
	 * yet may become relevant in the future (for example if the order of
	 * preceding/proceeding statement checks are changed).
	 * 
	 * @param displayName  The display name of the test
	 * @param containerCls A statement list container sub-type
	 * @param containeeCls A statement sub-type, an instance of which will be added
	 *                     to the container
	 * 
	 * @see {@link #testBody(Class, Class, int, int)} for more information
	 */
	@ParameterizedTest(name = "Mocked statement in middle: {2}")
	@MethodSource("genTestParams")
	public void test_StatementPosition_MalfunctioningStatementRetrieval(Class<? extends Statement> containeeCls,
			Class<? extends StatementListContainer> containerCls, String displayName) {
		final var lhsStartingLimit = new int[] { 0 };
		final var lhsCurrentLimit = new int[] { 0 };

		final var rhsStartingLimit = new int[] { 0 };
		final var rhsCurrentLimit = new int[] { 0 };

		/*
		 * A variant of the below, where lhsLastLimit and rhsLastLimit are dynamically
		 * computed. This way, the test remains more flexible to changes in similarity
		 * checking. lhsLastLimit and rhsLastLimit are the smallest indices, for which
		 * the similarity checking becomes ordinary (i.e. as if the statement retrieval
		 * method would never fail).
		 * 
		 * The indices in the actual implementation are in form of final arrays, in
		 * order to allow manipulating them from other methods, without having to
		 * declare them as members of the test class.
		 */

		// for (int i = 0: i < lhsLastLimit; i++) {
		// for (int j = 0; j < rhsLastLimit; j++) {
		// this.testBody(containeeCls, containerCls, i, j)
		// }
		// }

		while (lhsCurrentLimit[0] <= 0) {
			lhsCurrentLimit[0] = lhsStartingLimit[0];
			while (rhsCurrentLimit[0] <= 0) {
				lhsCurrentLimit[0] = lhsStartingLimit[0];
				rhsCurrentLimit[0] = rhsStartingLimit[0];
				this.testBody(containeeCls, containerCls, lhsCurrentLimit, rhsCurrentLimit);

				rhsStartingLimit[0] += 1;
			}
			rhsCurrentLimit[0] = 0;
			rhsStartingLimit[0] = 0;
			lhsStartingLimit[0] += 1;
		}
	}

	/**
	 * Tests if {@link Statement} mocks contained by their respective
	 * {@link StatementListContainer} mocks are similar, if
	 * {@code statementListContainer.getStatements()} malfunctions for both sides at
	 * some point.
	 * 
	 * @param containeeCls               The class of the statement that will be
	 *                                   placed into the mocked container
	 * @param containerCls               The class of the statement list container
	 *                                   sub-type, which will be mocked and used as
	 *                                   the container
	 * @param lhsStatementRetrievalCount The amount of times the left hand side
	 *                                   statement list container's
	 *                                   {@code getStatements()} method works as
	 *                                   intended. Once this count drops to 0, the
	 *                                   said method will return null instead.
	 * @param rhsStatementRetrievalCount The amount of times the right hand side
	 *                                   statement list container's
	 *                                   {@code getStatements()} method works as
	 *                                   intended. Once this count drops to 0, the
	 *                                   said method will return null instead.
	 */
	public void testBody(Class<? extends Statement> containeeCls, Class<? extends StatementListContainer> containerCls,
			final int[] lhsStatementRetrievalCount, final int[] rhsStatementRetrievalCount) {
		var slc1 = this.mockEObject(containerCls);
		var slc2 = this.mockEObject(containerCls);

		/*
		 * Mock the surrounding statements as well as the statements under test, because
		 * there is no other way to add them as statements to slc1 or slc2.
		 * 
		 * Preceding and proceeding statements should be similar, in order to cover more
		 * cases.
		 */
		var pred1 = this.mockLVS(this.createMinimalLV("lv1"), slc1);
		var st1 = this.mockEObjectWithContainer(containeeCls, slc1);
		var succ1 = this.mockLVS(this.createMinimalLV("lv2"), slc1);

		var pred2 = this.mockLVS(this.createMinimalLV("lv1"), slc2);
		var st2 = this.mockEObjectWithContainer(containeeCls, slc2);
		var succ2 = this.mockLVS(this.createMinimalLV("lv2"), slc2);

		var sts1 = new Statement[] { pred1, st1, succ1 };
		var sts2 = new Statement[] { pred2, st2, succ2 };

		var lhsCount = lhsStatementRetrievalCount[0];
		var rhsCount = rhsStatementRetrievalCount[0];

		var getStsCount1 = this.setUpSLCMock(slc1, sts1, lhsCount);
		var getStsCount2 = this.setUpSLCMock(slc2, sts2, rhsCount);

		/*
		 * Make sure that both preceding statements are similar and both proceeding
		 * statements are similar. However, no preceding and proceeding statement pair
		 * should be similar.
		 * 
		 * Keep in mind that them being similar will cause the similarity checking to
		 * use statement retrieval count of their container. Therefore, the statement
		 * retrieval count for both sides must be reset after each similarity check call
		 * below.
		 */
		this.assertSimilarityResultEquals(pred1, pred2, true, getStsCount1, lhsCount, getStsCount2, rhsCount);
		this.assertSimilarityResultEquals(pred1, succ2, false, getStsCount1, lhsCount, getStsCount2, rhsCount);
		this.assertSimilarityResultEquals(succ1, pred2, false, getStsCount1, lhsCount, getStsCount2, rhsCount);
		this.assertSimilarityResultEquals(succ1, succ2, true, getStsCount1, lhsCount, getStsCount2, rhsCount);

		/*
		 * The construction above leads to preceding and proceeding statements to be
		 * detected as similar, as statement retrieval fails. This in return causes the
		 * similarity result to be true, since both surrounding statements are assumed
		 * to be similar.
		 * 
		 * If statement positioning does not matter, st1 and st2 will always be similar.
		 * 
		 * If statement positioning does matter, similarity of st1 and st2 will depend
		 * on the similarity of their predecessors and successors (i.e. whether pred1
		 * and pred2 are similar, succ1 and succ2 are similar). If both are different,
		 * st1 and st2 are different too; otherwise st1 and st2 are similar:
		 * 
		 * Currently, st1 and st2 are similar in an ordinary comparison, where statement
		 * retrieval of their container works as intended. Therefore, if the statement
		 * retrieval count is high enough for both sides, the comparison will become an
		 * ordinary comparison. On the other hand, if statement retrieval count is low
		 * enough for both sides, none of their preceding/proceeding statements will be
		 * relevant.
		 * 
		 * Note: !(A ^ B) is the same as (A == B || !A == !B)
		 */
		var res = this.isSimilar(st1, st2);
		Assertions.assertEquals(
				!this.doesStatementPositionMatter(containeeCls) || !(getStsCount1[0] > -1 ^ getStsCount2[0] > -1), res);

		lhsStatementRetrievalCount[0] = getStsCount1[0];
		rhsStatementRetrievalCount[0] = getStsCount2[0];
	}

	/**
	 * Asserts that the similarity checking result of obj1 and obj2 is expectedVal
	 * and resets the getStatement call counts.
	 */
	private void assertSimilarityResultEquals(Object obj1, Object obj2, boolean expectedVal, final int[] lhsCallCount,
			int lhsStatementRetrievalCount, final int[] rhsCallCount, int rhsStatementRetrievalCount) {
		Assertions.assertEquals(expectedVal, this.isSimilar(obj1, obj2));
		// Reset call counts after each similarity check
		lhsCallCount[0] = lhsStatementRetrievalCount;
		rhsCallCount[0] = rhsStatementRetrievalCount;
	}

	/**
	 * Sets up the given {@link StatementListContainer} mock {@code slc} in a way
	 * that {@code slc.getStatements()} returns the expected output
	 * (statementsToContain in list form as {@code UniqueEList<Statement>}) only
	 * statementRetrievalCount times. Returns the array, which contains the
	 * statement retrieval count, in order to allow manipulating it from outside,
	 * such as resetting it.
	 * 
	 * @param slc                     A {@link StatementListContainer} mock to set
	 *                                up or reset
	 * @param statementsToContain     The statements that slc is supposed to contain
	 * @param statementRetrievalCount The amount of times
	 *                                {@code slc.getStatements()} returns the
	 *                                expected output (statementsToContain in list
	 *                                form) only statementRetrievalCount times
	 * 
	 * @return A final int array, which stores the amount of times
	 *         {@code slc.getStatements()} returns the expected value. Once it
	 *         reaches 0, the said method starts to return null instead. In other
	 *         words, If the value within the returned array is negative (i.e.
	 *         {@code 0 < arr[0]}), it means that the statement retrieval method
	 *         ({@code slc.getStatements()}) has returned null.
	 */
	private final int[] setUpSLCMock(StatementListContainer slc, Statement[] statementsToContain,
			int statementRetrievalCount) {

		final int[] getStatementsCallCount = new int[] { statementRetrievalCount };
		when(slc.getStatements()).thenAnswer(new Answer<EList<Statement>>() {
			@Override
			public EList<Statement> answer(InvocationOnMock arg0) throws Throwable {
				/*
				 * Decrement first and then check for >= 0.
				 * 
				 * This way, negative (i.e. 0 <) call counts signal that the statement retrieval
				 * returned null.
				 */
				getStatementsCallCount[0] -= 1;
				if (getStatementsCallCount[0] >= 0) {
					var list = new UniqueEList<Statement>();
					for (var st : statementsToContain) {
						list.add(st);
					}
					return list;
				} else {
					return null;
				}
			}
		});

		return getStatementsCallCount;
	}
}
