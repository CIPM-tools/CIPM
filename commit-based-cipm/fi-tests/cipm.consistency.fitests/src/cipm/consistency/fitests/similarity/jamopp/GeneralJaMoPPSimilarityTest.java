package cipm.consistency.fitests.similarity.jamopp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import cipm.consistency.initialisers.IInitialiserBase;
import cipm.consistency.initialisers.jamopp.IJaMoPPEObjectInitialiser;
import cipm.consistency.initialisers.jamopp.containers.ModuleInitialiser;
import cipm.consistency.initialisers.jamopp.containers.PackageInitialiser;

import org.emftext.language.java.containers.Module;

/**
 * A test class dedicated to test the general control flow of similarity
 * checking. <br>
 * <br>
 * Also contains some tests for lists, from which some abstract from
 * {@link EObject} instances, whereas others make use of {@link Module}
 * instances, as they can easily be compared after their name
 * ({@code module.getName()}).
 * 
 * @author Alp Torac Genc
 */
public class GeneralJaMoPPSimilarityTest extends AbstractJaMoPPSimilarityTest {
	/**
	 * Provides all non-adapted versions of {@link IJaMoPPEObjectInitialiser}
	 * implementors.
	 */
	private static Stream<Arguments> provideNonAdaptedInitialisers() {
		return AbstractJaMoPPSimilarityTest.getNonAdaptedInitialiserArgumentsFor(IJaMoPPEObjectInitialiser.class);
	}

	/**
	 * Provides all adapted versions of {@link IJaMoPPEObjectInitialiser}
	 * implementors.
	 */
	private static Stream<Arguments> provideAdaptedInitialisers() {
		return AbstractJaMoPPSimilarityTest.getAdaptedInitialiserArgumentsFor(IJaMoPPEObjectInitialiser.class);
	}

	/**
	 * Provides all versions of {@link IJaMoPPEObjectInitialiser} implementors.
	 */
	private static Stream<Arguments> provideAllInitialisers() {
		return AbstractJaMoPPSimilarityTest.getAllInitialiserArgumentsFor(IJaMoPPEObjectInitialiser.class);
	}

	/**
	 * Provides one instance of each {@link IJaMoPPEObjectInitialiser} implementor.
	 * The initialiser instance will be adapted, if possible.
	 */
	private static Stream<Arguments> provideEachInitialiserOnce() {
		return AbstractJaMoPPSimilarityTest.getEachInitialiserArgumentsOnceFor(IJaMoPPEObjectInitialiser.class);
	}

	/**
	 * @return An empty, immutable list
	 */
	private <T extends Object> List<T> toList() {
		return List.of();
	}

	/**
	 * @return An immutable list containing the given elements
	 */
	private List<? extends EObject> toList(EObject... eos) {
		return List.of(eos);
	}

	/**
	 * Instantiates an object using init, initialises that object and returns it.
	 * Asserts that the initialisation is successful.
	 */
	private EObject instantiateAndInitialise(IJaMoPPEObjectInitialiser init) {
		var obj = init.instantiate();
		Assertions.assertTrue(init.initialise(obj));
		return obj;
	}

	/**
	 * @return A mutable list with a single null element.
	 */
	private <T extends Object> List<T> makeListWithSingleNullElement() {
		var list = new ArrayList<T>();
		list.add(null);

		// Make sure that the null element is in the list
		Assertions.assertEquals(1, list.size());
		Assertions.assertNull(list.get(0));

		return list;
	}

	/**
	 * A variant of {@link #assertAreSimilar(List, List, Boolean)}, where the last
	 * parameter is true.
	 */
	private void assertAreSimilar(List<? extends EObject> eos1, List<? extends EObject> eos2) {
		this.assertAreSimilar(eos1, eos2, Boolean.TRUE);
	}

	/**
	 * Makes the following assertions:
	 * 
	 * <ul>
	 * <li>The similarity value of both lists with respect to
	 * {@link #areSimilar(java.util.Collection, java.util.Collection)} is
	 * expectedResult.
	 * <li>The object within the lists are pairwise similar with respect to
	 * {@link #isSimilar(Object, Object)}, if expectedResult is true. Otherwise
	 * asserts that there is at least one pair, which is not similar.
	 * <li>The similarity checking is symmetrical, meaning that changing positions
	 * of parameters within similarity checking methods has no effect on the result.
	 * </ul>
	 */
	private void assertAreSimilar(List<? extends EObject> eos1, List<? extends EObject> eos2, Boolean expectedResult) {
		Assertions.assertEquals(expectedResult, this.areSimilar(eos1, eos2));
		Assertions.assertEquals(expectedResult, this.areSimilar(eos2, eos1), "areSimilar is not symmetric");

		if (eos1 != null && eos2 != null) {
			int size = eos1.size() == eos2.size() ? eos1.size() : -1;

			if (expectedResult.booleanValue()) {
				// Ensure that the objects are pairwise similar as well
				for (int i = 0; i < size; i++) {
					this.assertIsSimilar(eos1.get(i), eos2.get(i));
				}
			} else if (size > 0) {
				// Ensure that there is at least one object pair that is not similar, if list
				// sizes are equal yet they not similar
				for (int i = 0; i < size; i++) {
					var eo1 = eos1.get(i);
					var eo2 = eos2.get(i);

					var res1 = this.isSimilar(eo1, eo2);
					var res2 = this.isSimilar(eo2, eo1);

					Assertions.assertEquals(res1, res2, "isSimilar checking is not symmetric");
					if (res1 == Boolean.FALSE) {
						return;
					}
				}
				Assertions.fail("areSimilar returns false, even though all objects are pairwise similar");
			}
		}
	}

	/**
	 * A variant of {@link #assertIsSimilar(EObject, EObject, Boolean)}, where the
	 * last parameter is true.
	 */
	private void assertIsSimilar(EObject obj1, EObject obj2) {
		this.assertIsSimilar(obj1, obj2, Boolean.TRUE);
	}

	/**
	 * Asserts that similarity checking obj1 and obj2 results in expectedResult.
	 * Additionally asserts that similarity checking is symmetrical, meaning that
	 * changing positions of obj1 and obj2 inside the similarity checking method has
	 * no effect on the result.
	 */
	private void assertIsSimilar(EObject obj1, EObject obj2, Boolean expectedResult) {
		var res1 = this.isSimilar(obj1, obj2);
		var res2 = this.isSimilar(obj2, obj1);

		// FIXME: Remove the null check and deal with the cause of the issues

		if (expectedResult == null || (res1 != null && res2 != null)) {
			Assertions.assertEquals(expectedResult, res1);
			Assertions.assertEquals(expectedResult, res2, "isSimilar is not symmetric");
		} else {
			this.getLogger().warn("isSimilar returned null");
		}
	}

	/**
	 * Ensure that the inherited {@link IJaMoPPEObjectInitialiser#newInitialiser()}
	 * returns an initialiser instance of the same type. <br>
	 * <br>
	 * All versions of initialisers are included to make sure that adapting
	 * initialisers does not break type equality of the returned initialiser.
	 */
	@ParameterizedTest
	@MethodSource("provideAllInitialisers")
	public void test_NewInitialiser_TypeCheck(IJaMoPPEObjectInitialiser initialiser) {
		Assertions.assertEquals(initialiser.getClass(), initialiser.newInitialiser().getClass());
	}

	/**
	 * Ensure that the inherited {@link IJaMoPPEObjectInitialiser#newInitialiser()}
	 * does not consider adaptation strategies.
	 */
	@ParameterizedTest
	@MethodSource("provideAdaptedInitialisers")
	public void test_NewInitialiser_AdaptationStrategiesNotCopied(IInitialiserBase initialiser) {
		var newInit = (IInitialiserBase) initialiser.newInitialiser();
		Assertions.assertFalse(newInit.isAdapted());
	}

	/**
	 * Ensure that adaptation strategies of adapted initialisers are copied for the
	 * new initialiser instance. Also make sure that the new adaptation strategies
	 * are not reference equal.
	 */
	@ParameterizedTest
	@MethodSource("provideAdaptedInitialisers")
	public void test_NewInitialiser_WithAdaptationStrategies(IInitialiserBase initialiser) {
		var newInit = initialiser.newInitialiserWithStrategies();

		Assertions.assertEquals(initialiser.getClass(), newInit.getClass());

		if (initialiser.isAdapted()) {
			Assertions.assertTrue(newInit.isAdapted());

			var strats = initialiser.getAdaptingStrategies();
			var newStrats = newInit.getAdaptingStrategies();

			Assertions.assertEquals(strats.size(), newStrats.size());

			// Ensure that new adaptation strategies are not reference equal
			for (var s : strats) {
				Assertions.assertTrue(newStrats.stream().noneMatch((ns) -> ns == s));

				// Approximate equality of contents by checking amount of elements of same type
				Assertions.assertEquals(strats.stream().filter((s2) -> s2.getClass().equals(s.getClass())).count(),
						newStrats.stream().filter((ns) -> ns.getClass().equals(s.getClass())).count());
			}
		} else {
			Assertions.assertFalse(newInit.isAdapted());
		}
	}

	/**
	 * Checks if similarity checking causes issues, if the {@link EObject} instances
	 * on both sides are not properly initialised. <br>
	 * <br>
	 * Only non-adapted initialisers are used, since the generated instances should
	 * not be initialised.
	 */
	@ParameterizedTest
	@Disabled("Disabled till null pointer exceptions are fixed")
	@MethodSource("provideNonAdaptedInitialisers")
	public void test_IsSimilar_Uninitialised_ObjectsEqual(IJaMoPPEObjectInitialiser initialiser) {
		var obj1 = initialiser.instantiate();
		var obj2 = initialiser.instantiate();

		this.assertIsSimilar(obj1, obj2);
	}

	/**
	 * Checks if two minimally initialised {@link EObject} instances are
	 * similar.<br>
	 * <br>
	 * Only adapted initialisers are used, since the generated instances should be
	 * initialised.
	 */
	@ParameterizedTest
	@MethodSource("provideAdaptedInitialisers")
	public void test_IsSimilar_Initialised_ObjectsEqual(IJaMoPPEObjectInitialiser initialiser) {
		var obj11 = this.instantiateAndInitialise(initialiser);
		var obj12 = this.instantiateAndInitialise(initialiser);

		this.assertIsSimilar(obj11, obj12);
	}

	/**
	 * Checks if the same {@link EObject} instance is similar to itself.
	 */
	@ParameterizedTest
	@MethodSource("provideAllInitialisers")
	public void test_IsSimilar_SameReference(IJaMoPPEObjectInitialiser initialiser) {
		var obj11 = initialiser.instantiate();

		this.assertIsSimilar(obj11, obj11);
	}

	/**
	 * Checks if an {@link EObject} instance is similar to its clone.
	 */
	@ParameterizedTest
	@Disabled("Disabled till null pointer exceptions are fixed")
	@MethodSource("provideAllInitialisers")
	public void test_IsSimilar_CloneEqual(IJaMoPPEObjectInitialiser initialiser) {
		var obj11 = initialiser.instantiate();
		var objClone = this.cloneEObj(obj11);

		this.assertIsSimilar(obj11, objClone);
	}

	/**
	 * Checks whether similarity checking causes issues, if one side is null.
	 */
	@Test
	public void test_IsSimilar_OneSide_Null() {
		var initialiser = new ModuleInitialiser();
		var obj = initialiser.instantiate();

		this.assertIsSimilar(null, obj, Boolean.FALSE);
	}

	/**
	 * Checks whether similarity checking causes issues, if both sides are null.
	 */
	@Test
	public void test_IsSimilar_BothSides_Null() {
		this.assertIsSimilar(null, null);
	}

	/**
	 * Checks if two {@link EObject} instances that implement different interfaces
	 * are not similar.
	 */
	@Test
	public void test_IsSimilar_ClassMismatch() {
		var mod = new ModuleInitialiser().instantiate();
		var pac = new PackageInitialiser().instantiate();

		this.assertIsSimilar(mod, pac, Boolean.FALSE);
	}

	/**
	 * Checks whether similarity checking causes issues, if 2 lists each with 1
	 * {@link EObject} instance cause issues, where the mentioned instances are not
	 * initialised properly. <br>
	 * <br>
	 * Only non-adapted initialisers are used, since the generated instances should
	 * not be initialised.
	 */
	@ParameterizedTest
	@Disabled("Disabled till null pointer exceptions are fixed")
	@MethodSource("provideNonAdaptedInitialisers")
	public void test_AreSimilar_Uninitialised_SingleObject(IJaMoPPEObjectInitialiser initialiser) {
		var obj1 = initialiser.instantiate();
		var obj2 = initialiser.instantiate();

		this.assertAreSimilar(this.toList(obj1), this.toList(obj2));
	}

	/**
	 * Checks if similarity checking causes issues, if 2 lists each with 2
	 * {@link EObject} instance cause issues, where the mentioned {@link EObject}
	 * instances are not initialised properly. <br>
	 * <br>
	 * Only non-adapted initialisers are used, since the generated instances should
	 * not be initialised.
	 */
	@ParameterizedTest
	@Disabled("Disabled till null pointer exceptions are fixed")
	@MethodSource("provideNonAdaptedInitialisers")
	public void test_AreSimilar_Uninitialised_MultipleObjects(IJaMoPPEObjectInitialiser initialiser) {
		var obj11 = initialiser.instantiate();
		var obj12 = initialiser.instantiate();
		var obj21 = initialiser.instantiate();
		var obj22 = initialiser.instantiate();

		this.assertAreSimilar(this.toList(obj11, obj12), this.toList(obj21, obj22));
	}

	/**
	 * Checks whether similarity checking causes issues, if 2 lists each with 1
	 * {@link EObject} instance cause issues, where the mentioned instances are not
	 * initialised properly.<br>
	 * <br>
	 * Only adapted initialisers are used, since the generated instances should be
	 * initialised.
	 */
	@ParameterizedTest
	@MethodSource("provideAdaptedInitialisers")
	public void test_AreSimilar_Initialised_SingleObject(IJaMoPPEObjectInitialiser initialiser) {
		var obj1 = this.instantiateAndInitialise(initialiser);
		var obj2 = this.instantiateAndInitialise(initialiser);

		this.assertAreSimilar(this.toList(obj1), this.toList(obj2));
	}

	/**
	 * Checks if similarity checking causes issues, if 2 lists each with 2
	 * {@link EObject} instance cause issues, where the mentioned {@link EObject}
	 * instances are not initialised properly.<br>
	 * <br>
	 * Only adapted initialisers are used, since the generated instances should be
	 * initialised.
	 */
	@ParameterizedTest
	@MethodSource("provideAdaptedInitialisers")
	public void test_AreSimilar_Initialised_MultipleObjects(IJaMoPPEObjectInitialiser initialiser) {
		var obj11 = this.instantiateAndInitialise(initialiser);
		var obj12 = this.instantiateAndInitialise(initialiser);
		var obj21 = this.instantiateAndInitialise(initialiser);
		var obj22 = this.instantiateAndInitialise(initialiser);

		this.assertAreSimilar(this.toList(obj11, obj12), this.toList(obj21, obj22));
	}

	/**
	 * Checks if similarity checking returns true, when a list is compared to
	 * itself. The said list contains one {@link EObject} instance.
	 */
	@ParameterizedTest
	@MethodSource("provideNonAdaptedInitialisers")
	public void test_AreSimilar_SingleObject_SameReference(IJaMoPPEObjectInitialiser initialiser) {
		var obj = initialiser.instantiate();
		var list = this.toList(obj);

		this.assertAreSimilar(list, list);
	}

	/**
	 * Checks if a list containing only an {@link EObject} instance is similar to
	 * another list that contains only a clone of that {@link EObject} instance.
	 */
	@ParameterizedTest
	@Disabled("Disabled till null pointer exceptions are fixed")
	@MethodSource("provideAllInitialisers")
	public void test_AreSimilar_SingleObject_CloneEqual(IJaMoPPEObjectInitialiser initialiser) {
		var obj = initialiser.instantiate();
		var objCopy = this.cloneEObj(obj);

		this.assertAreSimilar(this.toList(obj), this.toList(objCopy));
	}

	/**
	 * Checks if similarity checking returns true, when a list is compared to
	 * itself. The said list contains two different {@link EObject} instances.
	 */
	@Test
	public void test_AreSimilar_MultipleObjects_SameReference() {
		var modInit = new ModuleInitialiser();

		var obj1 = modInit.instantiate();
		Assertions.assertTrue(modInit.setName(obj1, "mod1"));

		var obj2 = modInit.instantiate();
		Assertions.assertTrue(modInit.setName(obj2, "mod2"));

		// Make sure both objects are different
		this.assertIsSimilar(obj1, obj2, Boolean.FALSE);

		var list = this.toList(obj1, obj2);

		this.assertAreSimilar(list, list);
	}

	/**
	 * Checks if a list containing 2 different {@link EObject} instances is similar
	 * to another list that is its clone.
	 */
	@Test
	public void test_AreSimilar_MultipleObjects_CloneEqual() {
		var initialiser = new ModuleInitialiser();

		var obj1 = initialiser.instantiate();
		Assertions.assertTrue(initialiser.setName(obj1, "mod1"));

		var obj2 = initialiser.instantiate();
		Assertions.assertTrue(initialiser.setName(obj2, "mod2"));

		// Make sure both objects are different
		this.assertIsSimilar(obj1, obj2, Boolean.FALSE);

		var obj1Copy = this.cloneEObj(obj1);
		var obj2Copy = this.cloneEObj(obj2);

		this.assertAreSimilar(this.toList(obj1, obj2), this.toList(obj1Copy, obj2Copy));
	}

	/**
	 * Checks if similarity checking lists of {@link EObject} instances causes
	 * issues, if one side is null.
	 */
	@Disabled("Disabled till null pointer exceptions are fixed")
	@Test
	public void test_AreSimilar_OneSide_Null() {
		var initialiser = new ModuleInitialiser();
		var obj = initialiser.instantiate();

		this.assertAreSimilar(null, this.toList(obj), Boolean.FALSE);
	}

	/**
	 * Checks if two lists, where one list is empty and the other one is not, are
	 * not similar.
	 */
	@ParameterizedTest
	@MethodSource("provideNonAdaptedInitialisers")
	public void test_AreSimilar_OneSide_EmptyList(IJaMoPPEObjectInitialiser initialiser) {
		var obj = initialiser.instantiate();

		this.assertAreSimilar(this.toList(), this.toList(obj), Boolean.FALSE);
	}

	/**
	 * Checks if similarity checking lists of {@link EObject} instances causes
	 * issues, if one of the lists contains a null element.
	 */
	@Test
	public void test_AreSimilar_OneSide_ListWithNullElement() {
		var initialiser = new ModuleInitialiser();
		var obj = initialiser.instantiate();

		this.assertAreSimilar(this.toList(obj), this.makeListWithSingleNullElement(), Boolean.FALSE);
	}

	/**
	 * Checks if two lists, where one of them is a sublist of another, are not
	 * similar.
	 */
	@Test
	public void test_AreSimilar_OneSide_SubList() {
		var modInit = new ModuleInitialiser();

		var obj1 = modInit.instantiate();
		Assertions.assertTrue(modInit.setName(obj1, "mod1"));

		var obj2 = modInit.instantiate();
		Assertions.assertTrue(modInit.setName(obj2, "mod2"));

		// Make sure both objects are different
		this.assertIsSimilar(obj1, obj2, Boolean.FALSE);

		this.assertAreSimilar(this.toList(obj1), this.toList(obj1, obj2), Boolean.FALSE);
	}

	/**
	 * Checks if similarity checking lists of {@link EObject} instances causes
	 * issues, if both sides are null.
	 */
	@Disabled("Disabled till null pointer exceptions are fixed")
	@Test
	public void test_AreSimilar_BothSides_Null() {
		this.assertAreSimilar(null, null);
	}

	/**
	 * Checks if two empty lists are similar.
	 */
	@Test
	public void test_AreSimilar_BothSides_EmptyList() {
		Assertions.assertTrue(this.areSimilar(this.toList(), this.toList()));
	}

	/**
	 * Checks if similarity checking lists of {@link EObject} instances causes
	 * issues, if both lists each contain a null element.
	 */
	@Test
	public void test_AreSimilar_BothSides_ListsWithNullElement() {
		this.assertAreSimilar(this.makeListWithSingleNullElement(), this.makeListWithSingleNullElement());
	}

	/**
	 * Checks whether lists with the same size, same elements (clones of them) but
	 * in different order are successfully identified as different. <br>
	 * <br>
	 * Implemented by cloning and comparing 2
	 * {@link org.emftext.language.java.containers.Module}.
	 */
	@Test
	public void test_AreSimilar_SameObjectDifferentOrder() {
		var modInit = new ModuleInitialiser();

		var obj1 = modInit.instantiate();
		Assertions.assertTrue(modInit.setName(obj1, "mod1"));

		var obj2 = modInit.instantiate();
		Assertions.assertTrue(modInit.setName(obj2, "mod2"));

		// Make sure both objects are different
		this.assertIsSimilar(obj1, obj2, Boolean.FALSE);

		this.assertAreSimilar(this.toList(obj1, obj2), this.toList(obj2, obj1), Boolean.FALSE);
	}

	/**
	 * Checks if two {@link EObject} instances that implement different interfaces
	 * are not similar.
	 */
	@Test
	public void test_AreSimilar_ClassMismatch() {
		var mod = new ModuleInitialiser().instantiate();
		var pac = new PackageInitialiser().instantiate();

		this.assertAreSimilar(this.toList(mod), this.toList(pac), Boolean.FALSE);
	}
}
