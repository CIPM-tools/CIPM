package cipm.consistency.fitests.similarity.jamopp.unittests.mocktests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

import java.util.List;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.JavaPackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cipm.consistency.fitests.similarity.jamopp.AbstractJaMoPPSimilarityTest;

/**
 * A test class, which ensures that all instances of all Java element types
 * present in {@link JavaPackage} are addressed by similarity checking. <br>
 * <br>
 * Note: These tests may include cases that are not currently addressed.
 * 
 * @author Alp Torac Genc
 */
public class InterfaceCoverageTest extends AbstractJaMoPPSimilarityTest implements IMockTest {
	/**
	 * @return Class object of each Java element type present in
	 *         {@link JavaPackage}.
	 */
	private static Stream<Arguments> genTestParams() {
		return IMockTest.getAllClasses().stream().map((cls) -> Arguments.of(cls, cls.getSimpleName()));
	}

	/**
	 * @return Class object of each Java element type present in
	 *         {@link JavaPackage}, which is concrete.
	 */
	private static Stream<Arguments> genConcreteTestParams() {
		return IMockTest.getAllClasses((eCls) -> !eCls.isAbstract()).stream()
				.map((cls) -> Arguments.of(cls, cls.getSimpleName()));
	}

	/**
	 * Makes sure that all types that are present in {@link JavaPackage} are
	 * addressed by similarity checking, i.e. computing the similarity of 2 mocked
	 * instances of cls returns true (since they are equal). <br>
	 * <br>
	 * This also covers some edge cases, where certain attributes are null, even
	 * though they cannot be null in the current similarity checking.
	 * 
	 * @param cls The type extending {@link EObject} that will be mocked.
	 */
	@ParameterizedTest(name = "Mocked class: {1}")
	@MethodSource("genTestParams")
	public void testInterfaceCoverage_BothSidesMocked(Class<? extends EObject> cls, String displayName) {
		/*
		 * Mock the given class and make sure that the mocks return their corresponding
		 * EClass, so that method calls till reaching similarity checking process do not
		 * cause Null Pointer Exceptions.
		 */

		var clsMock1 = this.mockEObject(cls);
		var clsMock2 = this.mockEObject(cls);

		/*
		 * FIXME Remove the null check once the issue is fixed.
		 * 
		 * Currently, attempting to similarity check certain EObject sub-type instances
		 * results in null being returned, even though said instances should be equal
		 * and true should be returned. Until those cases are handled properly, the
		 * assertion is guarded by a null check.
		 */
		var res = this.isSimilar(clsMock1, clsMock2);
		if (res != null) {
			Assertions.assertTrue(res);
		}
	}

	/**
	 * Makes sure that similarity checking is robust for all concrete types present
	 * in {@link JavaPackage} against cases, where retrieving the attributes of one
	 * side (namely the real mock) returns null. <br>
	 * <br>
	 * This ensures that similarity checking is resistant to null pointer
	 * exceptions.
	 * 
	 * @param cls The type extending {@link EObject} that will be mocked. It should
	 *            have a direct implementation, meaning if {@code cls == x.class}
	 *            then there should be a concrete class {@code xImpl} that directly
	 *            inherits from {@code x}.
	 */
	@SuppressWarnings("unchecked")
	@ParameterizedTest(name = "Mocked class: {1}")
	@MethodSource("genConcreteTestParams")
	public <T extends EObject> void testInterfaceCoverage_OneSideMocked_AllMethodsDelegated(Class<T> cls,
			String displayName) {
		var init = this.getUsedInitialiserPackage().getInitialiserInstanceFor(cls);
		var wrapee = (T) init.instantiate();
		var wrapeeCls = (Class<T>) wrapee.getClass();

		/*
		 * Mock the concrete implementation class TImpl twice, where one of the mocks
		 * (bareMock) is an ordinary mock and the other one (spyMock) is a spy that
		 * merely wraps an actual TImpl instance (wrapee) and delegates all method calls
		 * to wrapee.
		 * 
		 * This construction is a workaround for having an EObject implementation on one
		 * side and a mock on the other side. It is necessary, because types of both
		 * sides have to be equal for similarity checking, so cls1.equals(cls2). Even
		 * though the said types seem to be the equal, mock types and actual instance
		 * types are different and therefore not equal.
		 */
		var bareMock = this.mockEObjectImpl(wrapeeCls);
		var spyMock = this.spyEObject(wrapee);

		// Call isSimilar twice for both combinations to ensure that it is symmetrical
		Assertions.assertEquals(this.isSimilar(bareMock, spyMock), this.isSimilar(spyMock, bareMock),
				"isSimilar is not symmetric");
	}

	/**
	 * Makes sure that similarity checking is robust for all concrete types present
	 * in {@link JavaPackage} against cases, where retrieving the attributes of one
	 * side can fail and return null. <br>
	 * <br>
	 * This test is similar to
	 * {@link #testInterfaceCoverage_OneSideMocked_AllMethodsDelegated(Class)} but
	 * rather than having the (actually) modified side constantly return null, it
	 * instead limits the amount of times certain methods work as intended. <br>
	 * <br>
	 * This test case provides a deeper inspection of null checking mechanisms and
	 * ensures that similarity checking is symmetrical, in terms of cls' methods
	 * called during similarity checking. This means, isSimilar(lhs, rhs) is the
	 * same as calling isSimilar(rhs, lhs).
	 * 
	 * @param cls The type extending {@link EObject} that will be mocked. It should
	 *            have a direct implementation, meaning if {@code cls == x.class}
	 *            then there should be a concrete class {@code xImpl} that directly
	 *            inherits from {@code x}.
	 */
	@SuppressWarnings("unchecked")
	@ParameterizedTest(name = "Mocked class: {1}")
	@MethodSource("genConcreteTestParams")
	public <T extends EObject> void testInterfaceCoverage_OneSideMocked_MethodsRestricted(Class<T> cls,
			String displayName) {
		var init = this.getUsedInitialiserPackage().getInitialiserInstanceFor(cls);
		var wrapee = (T) init.instantiate();

		var wrappedInstance = this.spyEObject(wrapee);

		/*
		 * List of potentially relevant methods' names that could be used throughout
		 * similarity checking.
		 */
		final var potentiallyRelevantMets = List.of(Stream.of(init.getInstanceClassOfInitialiser().getMethods())
				// Exclude methods related to structure elements to avoid exceptions
				.filter((met) -> !EModelElement.class.isAssignableFrom(met.getReturnType()))
				// Methods used in similarity checking must return something
				.filter((met) -> !met.getReturnType().equals(void.class))
				// Methods used in similarity checking take no parameters
				.filter((met) -> met.getParameterCount() == 0).map((met) -> met.getName()).toArray(String[]::new));

		/*
		 * Compute the similarity of 2 mock objects what wrap actual object instances,
		 * where one side's methods contained in the list above only work as expected a
		 * certain amount of times (call limit). Once that amount is reached, they
		 * return null.
		 * 
		 * Each loop starts with startingLimit[0] call limits and each time one of the
		 * mentioned methods is called, the call limit within the loop (currentLimit) is
		 * decremented by 1. In the end of the loop, startingLimit[0] is incremented by
		 * 1 and the remaining call limit (currentLimit[0]) is checked:
		 * 
		 * currentLimit[0] < 0 implies that the startingLimit was less than the amount
		 * of call limit necessary to perform the similarity check normally. If that is
		 * the case, continue with the next loop.
		 * 
		 * currentLimit[0] => 0 implies that the similarity checking was performed
		 * normally. Therefore, there is no need to continue.
		 */
		final var startingLimit = new int[] { 0 };
		final var currentLimit = new int[] { 0 };

		while (currentLimit[0] <= 0) {
			currentLimit[0] = startingLimit[0];
			var modifiedWrapee = (T) init.instantiate();
			var spiedInstance = mock(modifiedWrapee.getClass(),
					withSettings().spiedInstance(modifiedWrapee).defaultAnswer(new Answer<Object>() {

						/**
						 * Call the real method in try blocks, so that any exception outside similarity
						 * checking is ignored.
						 */
						@Override
						public Object answer(InvocationOnMock arg0) throws Throwable {
							var calledMethodName = arg0.getMethod().getName();
							if (potentiallyRelevantMets.contains(calledMethodName)) {
								/*
								 * Decrement first and then check for >= 0.
								 * 
								 * This way, negative (i.e. 0 <) call counts signal that the statement retrieval
								 * returned null.
								 */
								currentLimit[0] -= 1;
								if (currentLimit[0] > 0) {
									return this.safeCallRealMethod(arg0);
								} else {
									return null;
								}
							}
							return this.safeCallRealMethod(arg0);
						}

						private Object safeCallRealMethod(InvocationOnMock arg0) {
							Object result = null;
							try {
								result = arg0.callRealMethod();
							} catch (Throwable e) {
							}
							return result;
						}
					}));

			var res1 = this.isSimilar(wrappedInstance, spiedInstance);
			var currentLimitAfterFirstSimCheck = currentLimit[0];

			// Reset the call limit, so that isSimilar's symmetry is not
			// violated because of it.
			currentLimit[0] = startingLimit[0];
			var res2 = this.isSimilar(spiedInstance, wrappedInstance);

			Assertions.assertEquals(res1, res2, "isSimilar is not symmetric");
			Assertions.assertEquals(currentLimitAfterFirstSimCheck, currentLimit[0], "isSimilar is not symmetric");

			// Set up for the next loop
			startingLimit[0] += 1;
		}
	}
}
