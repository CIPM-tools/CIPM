package cipm.consistency.initialisers.tests;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cipm.consistency.initialisers.IInitialiserAdapterStrategy;
import cipm.consistency.initialisers.IInitialiserBase;
import cipm.consistency.initialisers.tests.dummy.packages.DummyInitialiserA;
import cipm.consistency.initialisers.tests.dummy.packages.DummyInitialiserC;
import cipm.consistency.initialisers.tests.dummy.packages.ObjAInitStrat;
import cipm.consistency.initialisers.tests.dummy.packages.ObjCFirstInitStepStrat;
import cipm.consistency.initialisers.tests.dummy.packages.ObjCSecondInitStepStrat;

/**
 * Contains tests for {@link IInitialiserBase} over dummy elements, with a focus
 * on the interaction with {@link IInitialiserAdapterStrategy}.
 * 
 * @author Alp Torac Genc
 */
public class InitialiserBaseTests {
	/**
	 * Shortcut for {@code assertAdapted(init, true)}
	 */
	private void assertAdapted(IInitialiserBase init) {
		this.assertAdapted(init, true);
	}

	/**
	 * Assert that the given initialiser is being adapted by at least one adaptation
	 * strategy.
	 */
	private void assertAdapted(IInitialiserBase init, boolean isAdapted) {
		Assertions.assertEquals(init.getAdaptingStrategyCount(), init.getAdaptingStrategies().size());
		Assertions.assertEquals(isAdapted, init.isAdapted());
		Assertions.assertEquals(isAdapted, init.getAdaptingStrategyCount() > 0);
	}

	/**
	 * Assert that all given adaptation strategies are adapting the given
	 * initialiser.
	 */
	private void assertAdaptedBy(IInitialiserAdapterStrategy[] strats, IInitialiserBase init) {
		if (strats != null && strats.length > 0) {
			var adaptingStrats = init.getAdaptingStrategies();
			Assertions.assertEquals(strats.length, adaptingStrats.size());
			this.assertAdapted(init);
			for (var strat : strats) {
				Assertions.assertTrue(adaptingStrats.contains(strat));
			}
		} else {
			this.assertAdapted(init, false);
		}
	}

	/**
	 * Add the given adaptation strategy to the given initialiser and make the
	 * necessary assertions before and after insertion.
	 */
	private void addAndAssertAdded(IInitialiserAdapterStrategy strat, IInitialiserBase init) {
		var priorStrats = init.getAdaptingStrategies();
		Assertions.assertFalse(priorStrats.contains(strat));

		var allStrats = new ArrayList<IInitialiserAdapterStrategy>();
		allStrats.addAll(priorStrats);
		this.assertAdaptedBy(allStrats.toArray(IInitialiserAdapterStrategy[]::new), init);

		allStrats.add(strat);
		init.addAdaptingStrategy(strat);
		this.assertAdaptedBy(allStrats.toArray(IInitialiserAdapterStrategy[]::new), init);

		Assertions.assertEquals(priorStrats.size() + 1, init.getAdaptingStrategyCount());
	}

	/**
	 * Test adding a non-empty array of adaptation strategies to an initialiser
	 * during its construction.
	 */
	@Test
	public void test_AddStrategiesToInitAtConstruction() {
		var stratOne = new ObjCFirstInitStepStrat();
		var stratTwo = new ObjCSecondInitStepStrat();
		var stratArr = new IInitialiserAdapterStrategy[] { stratOne, stratTwo };

		var init = new DummyInitialiserC(stratArr);

		this.assertAdaptedBy(stratArr, init);
	}

	/**
	 * Test adding an empty array of adaptation strategies to an initialiser during
	 * its construction.
	 */
	@Test
	public void test_AddStrategiesToInitAtConstruction_EmptyArray() {
		var stratArr = new IInitialiserAdapterStrategy[] {};

		var init = new DummyInitialiserC(stratArr);

		this.assertAdaptedBy(stratArr, init);
	}

	/**
	 * Test adding a null array of adaptation strategies to an initialiser during
	 * its construction.
	 */
	@Test
	public void test_AddStrategiesToInitAtConstruction_NullArray() {
		var init = new DummyInitialiserC(null);
		this.assertAdapted(init, false);
	}

	/**
	 * Test adding adaptation strategies to an initialiser. Strategies are added one
	 * at a time.
	 */
	@Test
	public void test_AddStrategy_OneAtATime() {
		var init = new DummyInitialiserA();

		this.assertAdapted(init, false);

		var stratOne = new ObjAInitStrat();
		init.addAdaptingStrategy(stratOne);
		this.assertAdaptedBy(new IInitialiserAdapterStrategy[] { stratOne }, init);

		var stratTwo = new ObjAInitStrat();
		init.addAdaptingStrategy(stratTwo);
		this.assertAdaptedBy(new IInitialiserAdapterStrategy[] { stratOne, stratTwo }, init);
	}

	/**
	 * Test adding adaptation strategies to an initialiser. A strategy array is
	 * added to the initialiser.
	 */
	@Test
	public void test_AddStrategy_StrategyArray() {
		var init = new DummyInitialiserA();
		this.assertAdapted(init, false);

		var stratOne = new ObjAInitStrat();
		var stratTwo = new ObjAInitStrat();
		var stratArr = new IInitialiserAdapterStrategy[] { stratOne, stratTwo };

		init.addAdaptingStrategies(stratArr);
		this.assertAdaptedBy(stratArr, init);
	}

	/**
	 * Test adding adaptation strategies to an initialiser. A strategy array with a
	 * null element is added to the initialiser.
	 */
	@Test
	public void test_AddStrategy_StrategyArrayWithNull() {
		var init = new DummyInitialiserA();
		this.assertAdapted(init, false);

		var stratOne = new ObjAInitStrat();
		var stratTwo = new ObjAInitStrat();
		var stratArr = new IInitialiserAdapterStrategy[] { stratOne, null, stratTwo };

		init.addAdaptingStrategies(stratArr);
		this.assertAdaptedBy(new IInitialiserAdapterStrategy[] { stratOne, stratTwo }, init);
	}

	/**
	 * Test adding an empty array of adaptation strategies to an initialiser.
	 */
	@Test
	public void test_AddStrategy_EmptyArray() {
		var init = new DummyInitialiserA();
		this.assertAdapted(init, false);

		var stratArr = new IInitialiserAdapterStrategy[] {};

		init.addAdaptingStrategies(stratArr);
		this.assertAdapted(init, false);
	}

	/**
	 * Test adding a null array (of adaptation strategies) to an initialiser.
	 */
	@Test
	public void test_AddStrategy_NullArray() {
		var init = new DummyInitialiserA();
		this.assertAdapted(init, false);

		init.addAdaptingStrategies(null);
		this.assertAdapted(init, false);
	}

	/**
	 * Test removing (existing) adaptation strategies from an initialiser.
	 */
	@Test
	public void test_RemoveAdapterStrategy() {
		var init = new DummyInitialiserA();
		var stratOne = new ObjAInitStrat();
		var stratTwo = new ObjAInitStrat();
		init.addAdaptingStrategy(stratOne);
		init.addAdaptingStrategy(stratTwo);
		this.assertAdaptedBy(new IInitialiserAdapterStrategy[] { stratOne, stratTwo }, init);

		init.removeAdaptingStrategy(stratOne);
		this.assertAdaptedBy(new IInitialiserAdapterStrategy[] { stratTwo }, init);

		init.removeAdaptingStrategy(stratTwo);
		this.assertAdaptedBy(null, init);
	}

	/**
	 * Test removing a non-existent adaptation strategy from an initialiser.
	 */
	@Test
	public void test_RemoveAdapterStrategy_NonExistent() {
		var init = new DummyInitialiserA();
		var strat = new ObjAInitStrat();

		this.assertAdapted(init, false);
		init.removeAdaptingStrategy(strat);
		this.assertAdapted(init, false);
	}

	/**
	 * Test removing a non-existent adaptation strategy from an initialiser.
	 */
	@Test
	public void test_RemoveAdapterStrategy_NullCheck() {
		var init = new DummyInitialiserA();

		this.assertAdapted(init, false);
		init.removeAdaptingStrategy(null);
		this.assertAdapted(init, false);
	}

	/**
	 * Test removing an existing adaptation strategy from an initialiser twice.
	 */
	@Test
	public void test_RemoveAdapterStrategy_Twice() {
		var init = new DummyInitialiserA();
		var stratOne = new ObjAInitStrat();
		var stratTwo = new ObjAInitStrat();
		init.addAdaptingStrategy(stratOne);
		init.addAdaptingStrategy(stratTwo);
		this.assertAdaptedBy(new IInitialiserAdapterStrategy[] { stratOne, stratTwo }, init);

		init.removeAdaptingStrategy(stratOne);
		this.assertAdaptedBy(new IInitialiserAdapterStrategy[] { stratTwo }, init);
		var stratsAfterFirstRemoval = init.getAdaptingStrategies();

		init.removeAdaptingStrategy(stratOne);
		this.assertAdaptedBy(new IInitialiserAdapterStrategy[] { stratTwo }, init);

		// Make sure that all other adapting initialisers are still there after the
		// second removal attempt
		Assertions.assertTrue(init.getAdaptingStrategies().containsAll(stratsAfterFirstRemoval));
	}

	/**
	 * Test cleaning all adaptation strategies from an initialiser, i.e. removing
	 * all such strategies at once.
	 */
	@Test
	public void test_CleanAdapterStrategy() {
		var init = new DummyInitialiserA();
		var stratOne = new ObjAInitStrat();
		var stratTwo = new ObjAInitStrat();
		init.addAdaptingStrategy(stratOne);
		init.addAdaptingStrategy(stratTwo);
		this.assertAdaptedBy(new IInitialiserAdapterStrategy[] { stratOne, stratTwo }, init);

		init.cleanAdaptingStrategy();
		this.assertAdapted(init, false);
	}

	/**
	 * Test new initialiser creation with adapting strategies, where the original
	 * initialiser has no adapting strategies.
	 */
	@Test
	public void test_NewInitialiserWithStrategy_NoStrategies() {
		var init = new DummyInitialiserC();
		this.assertAdapted(init, false);
		var newInit = init.newInitialiserWithStrategies();
		this.assertAdapted(newInit, false);

		Assertions.assertFalse(init == newInit);
	}

	/**
	 * Test new initialiser creation with adapting strategies.
	 */
	@Test
	public void test_NewInitialiserWithStrategy() {
		var init = new DummyInitialiserC();
		var stratOne = new ObjCFirstInitStepStrat();
		var stratTwo = new ObjCSecondInitStepStrat();

		this.addAndAssertAdded(stratOne, init);
		this.addAndAssertAdded(stratTwo, init);
		this.assertAdapted(init, true);

		var newInit = init.newInitialiserWithStrategies();
		this.assertAdapted(newInit, true);

		Assertions.assertFalse(init == newInit);
		Assertions.assertFalse(newInit.getAdaptingStrategies().contains(stratOne));
		Assertions.assertFalse(newInit.getAdaptingStrategies().contains(stratTwo));
	}

	/**
	 * Test new initialiser creation without adapting strategies, even though the
	 * original initialiser has adapting strategies.
	 */
	@Test
	public void test_NewInitialiser_WithStrategies() {
		var init = new DummyInitialiserC();
		var stratOne = new ObjCFirstInitStepStrat();
		var stratTwo = new ObjCSecondInitStepStrat();

		this.addAndAssertAdded(stratOne, init);
		this.addAndAssertAdded(stratTwo, init);
		this.assertAdapted(init, true);

		var newInit = init.newInitialiser();
		this.assertAdapted(newInit, false);

		Assertions.assertFalse(init == newInit);
	}

	/**
	 * Test initialising an object generated from an initialiser without adapting
	 * it.
	 */
	@Test
	public void test_Initialisation_WithoutStrategy() {
		var init = new DummyInitialiserC();
		var obj = init.instantiate();
		this.assertAdapted(init, false);
		// No initialisation steps to fail here, so true
		Assertions.assertTrue(init.initialise(obj));

		Assertions.assertFalse(obj.isInitialisationStepOneDone());
		Assertions.assertFalse(obj.isInitialisationStepTwoDone());
	}

	/**
	 * Test initialising an object generated from an initialiser without adapting it
	 * all the way.
	 */
	@Test
	public void test_SingleInitialisation_PartialSuccess() {
		var init = new DummyInitialiserC();
		var obj = init.instantiate();

		this.addAndAssertAdded(new ObjCFirstInitStepStrat(), init);
		Assertions.assertTrue(init.initialise(obj));
		Assertions.assertTrue(obj.isInitialisationStepOneDone());
		Assertions.assertFalse(obj.isInitialisationStepTwoDone());
	}

	/**
	 * Test initialising an object generated from an initialiser with a failing
	 * adaptation strategy.
	 */
	@Test
	public void test_SingleInitialisation_Failure() {
		var init = new DummyInitialiserC();
		var obj = init.instantiate();

		this.addAndAssertAdded(new ObjCFirstInitStepStrat(false), init);
		Assertions.assertFalse(init.initialise(obj));
		Assertions.assertFalse(obj.isInitialisationStepOneDone());
		Assertions.assertFalse(obj.isInitialisationStepTwoDone());
	}

	/**
	 * Test initialising an object generated from an adapted initialiser, where the
	 * first adaptation strategy succeeds but the second one fails.
	 */
	@Test
	public void test_SingleInitialisation_LateConflict() {
		var init = new DummyInitialiserC();
		var obj = init.instantiate();

		this.addAndAssertAdded(new ObjCFirstInitStepStrat(), init);
		this.addAndAssertAdded(new ObjCSecondInitStepStrat(false), init);
		Assertions.assertFalse(init.initialise(obj));
		Assertions.assertTrue(obj.isInitialisationStepOneDone());
		Assertions.assertFalse(obj.isInitialisationStepTwoDone());
	}

	/**
	 * Test initialising an object generated from an adapted initialiser, where the
	 * first adaptation strategy fails and stops the second one from working, even
	 * though the second one would succeed.
	 */
	@Test
	public void test_SingleInitialisation_FailEarlyAtConflict() {
		var init = new DummyInitialiserC();
		var obj = init.instantiate();

		this.addAndAssertAdded(new ObjCFirstInitStepStrat(false), init);
		this.addAndAssertAdded(new ObjCFirstInitStepStrat(), init);
		Assertions.assertFalse(init.initialise(obj));
		Assertions.assertFalse(obj.isInitialisationStepOneDone());
		Assertions.assertFalse(obj.isInitialisationStepTwoDone());
	}

	/**
	 * Test initialising an object generated from a properly adapted initialiser.
	 */
	@Test
	public void test_SingleInitialisation_AtOnce() {
		var init = new DummyInitialiserC();
		var obj = init.instantiate();

		this.addAndAssertAdded(new ObjCFirstInitStepStrat(), init);
		this.addAndAssertAdded(new ObjCSecondInitStepStrat(), init);
		Assertions.assertTrue(init.initialise(obj));
		Assertions.assertTrue(obj.isInitialisationStepOneDone());
		Assertions.assertTrue(obj.isInitialisationStepTwoDone());
	}

	/**
	 * Test initialising an object generated from an initialiser but add necessary
	 * adaptation strategies over multiple steps.
	 */
	@Test
	public void test_MultipleInitialisations() {
		var init = new DummyInitialiserC();
		var obj = init.instantiate();

		this.addAndAssertAdded(new ObjCFirstInitStepStrat(), init);
		Assertions.assertTrue(init.initialise(obj));
		Assertions.assertTrue(obj.isInitialisationStepOneDone());
		Assertions.assertFalse(obj.isInitialisationStepTwoDone());

		this.addAndAssertAdded(new ObjCSecondInitStepStrat(), init);
		Assertions.assertTrue(init.initialise(obj));
		Assertions.assertTrue(obj.isInitialisationStepOneDone());
		Assertions.assertTrue(obj.isInitialisationStepTwoDone());
	}
}
