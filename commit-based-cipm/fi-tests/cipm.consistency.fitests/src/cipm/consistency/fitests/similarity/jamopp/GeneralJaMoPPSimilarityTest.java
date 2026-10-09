package cipm.consistency.fitests.similarity.jamopp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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
//	private static final org.emftext.language.java.containers.Module modInstance = ContainersFactory.eINSTANCE
//			.createModule();
//	private static final org.emftext.language.java.containers.Package pacInstance = ContainersFactory.eINSTANCE
//			.createPackage();

	/**
	 * Provides all versions of {@link EObjectInstantiator} implementors.
	 */
	private static Stream<Arguments> provideArguments() {
		return JaMoPPArguments.getAllConcreteClassesAsArgs();
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
		var listSim1 = this.areSimilar(eos1, eos2);
		var listSim2 = this.areSimilar(eos2, eos1);

		Assertions.assertEquals(expectedResult, listSim1);
		Assertions.assertEquals(expectedResult, listSim2, "areSimilar is not symmetric");

		this.reportIfSimCheckResultNull(listSim1, listSim2);

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

		Assertions.assertEquals(expectedResult, res1);
		Assertions.assertEquals(expectedResult, res2, "isSimilar is not symmetric");

		this.reportIfSimCheckResultNull(res1, res2);
	}

	private void reportIfSimCheckResultNull(Boolean res1, Boolean res2) {
		if (res1 == null || res2 == null) {
			this.getLogger().warn("Similarity checking returned null");
		}
	}

	/**
	 * Checks if similarity checking causes issues, if the {@link EObject} instances
	 * on both sides are not properly initialised. <br>
	 * <br>
	 * Only non-adapted initialisers are used, since the generated instances should
	 * not be initialised.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void test_IsSimilar_ObjectsEqual(Class<? extends EObject> cls, String displayName) {
		var obj1 = getAPI().createNewX(cls);
		var obj2 = getAPI().createNewX(cls);

		this.assertIsSimilar(obj1, obj2);
	}

	/**
	 * Checks if the same {@link EObject} instance is similar to itself.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void test_IsSimilar_SameReference(Class<? extends EObject> cls, String displayName) {
		var obj11 = getAPI().createNewX(cls);

		this.assertIsSimilar(obj11, obj11);
	}

	/**
	 * Checks if an {@link EObject} instance is similar to its clone.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void test_IsSimilar_CloneEqual(Class<? extends EObject> cls, String displayName) {
		var obj11 = getAPI().createNewX(cls);
		var objClone = this.cloneEObj(obj11);

		this.assertIsSimilar(obj11, objClone);
	}

	/**
	 * Checks whether similarity checking causes issues, if one side is null.
	 */
	@Test
	public void test_IsSimilar_OneSide_Null() {
		this.assertIsSimilar(null, getAPI().createNewModule(), Boolean.FALSE);
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
		this.assertIsSimilar(getAPI().createNewModule(), getAPI().createNewPackage(), Boolean.FALSE);
	}

	/**
	 * Checks whether similarity checking causes issues, if 2 lists each with 1
	 * {@link EObject} instance cause issues, where the mentioned instances are not
	 * initialised properly. <br>
	 * <br>
	 * Only non-adapted initialisers are used, since the generated instances should
	 * not be initialised.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void test_AreSimilar_SingleObject(Class<? extends EObject> cls, String displayName) {
		var obj1 = getAPI().createNewX(cls);
		var obj2 = getAPI().createNewX(cls);

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
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void test_AreSimilar_MultipleObjects(Class<? extends EObject> cls, String displayName) {
		var obj11 = getAPI().createNewX(cls);
		var obj12 = getAPI().createNewX(cls);
		var obj21 = getAPI().createNewX(cls);
		var obj22 = getAPI().createNewX(cls);

		this.assertAreSimilar(this.toList(obj11, obj12), this.toList(obj21, obj22));
	}

	/**
	 * Checks if similarity checking returns true, when a list is compared to
	 * itself. The said list contains one {@link EObject} instance.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void test_AreSimilar_SingleObject_SameReference(Class<? extends EObject> cls, String displayName) {
		var obj = getAPI().createNewX(cls);
		var list = this.toList(obj);

		this.assertAreSimilar(list, list);
	}

	/**
	 * Checks if a list containing only an {@link EObject} instance is similar to
	 * another list that contains only a clone of that {@link EObject} instance.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void test_AreSimilar_SingleObject_CloneEqual(Class<? extends EObject> cls, String displayName) {
		var obj = getAPI().createNewX(cls);
		var objCopy = this.cloneEObj(obj);

		this.assertAreSimilar(this.toList(obj), this.toList(objCopy));
	}

	/**
	 * Checks if similarity checking returns true, when a list is compared to
	 * itself. The said list contains two different {@link EObject} instances.
	 */
	@Test
	public void test_AreSimilar_MultipleObjects_SameReference() {
		var list = this.toList(getAPI().createNewModule(), getAPI().createNewPackage());

		this.assertAreSimilar(list, list);
	}

	/**
	 * Checks if a list containing 2 different {@link EObject} instances is similar
	 * to another list that is its clone.
	 */
	@Test
	public void test_AreSimilar_MultipleObjects_CloneEqual() {
		var obj1 = getAPI().createNewModule();
		var obj2 = getAPI().createNewPackage();

		var obj1Copy = this.cloneEObj(obj1);
		var obj2Copy = this.cloneEObj(obj2);

		this.assertAreSimilar(this.toList(obj1, obj2), this.toList(obj1Copy, obj2Copy));
	}

	/**
	 * Checks if similarity checking lists of {@link EObject} instances causes
	 * issues, if one side is null.
	 */
	@Test
	public void test_AreSimilar_OneSide_Null() {
		this.assertAreSimilar(null, this.toList(getAPI().createNewModule()), Boolean.FALSE);
	}

	/**
	 * Checks if two lists, where one list is empty and the other one is not, are
	 * not similar.
	 */
	@ParameterizedTest(name = "{1}")
	@MethodSource("provideArguments")
	public void test_AreSimilar_OneSide_EmptyList(Class<? extends EObject> cls, String displayName) {
		var obj = getAPI().createNewX(cls);

		this.assertAreSimilar(this.toList(), this.toList(obj), Boolean.FALSE);
	}

	/**
	 * Checks if similarity checking lists of {@link EObject} instances causes
	 * issues, if one of the lists contains a null element.
	 */
	@Test
	public void test_AreSimilar_OneSide_ListWithNullElement() {
		this.assertAreSimilar(this.toList(getAPI().createNewModule()), this.makeListWithSingleNullElement(),
				Boolean.FALSE);
	}

	/**
	 * Checks if two lists, where one of them is a sublist of another, are not
	 * similar.
	 */
	@Test
	public void test_AreSimilar_OneSide_SubList() {
		var obj1 = getAPI().createNewModule();
		var obj2 = getAPI().createNewPackage();

		this.assertAreSimilar(this.toList(obj1), this.toList(obj1, obj2), Boolean.FALSE);
	}

	/**
	 * Checks if similarity checking lists of {@link EObject} instances causes
	 * issues, if both sides are null.
	 */
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
		var obj1 = getAPI().createNewModule();
		var obj2 = getAPI().createNewPackage();

		this.assertAreSimilar(this.toList(obj1, obj2), this.toList(obj2, obj1), Boolean.FALSE);
	}

	/**
	 * Checks if two {@link EObject} instances that implement different interfaces
	 * are not similar.
	 */
	@Test
	public void test_AreSimilar_ClassMismatch() {
		var obj1 = getAPI().createNewModule();
		var obj2 = getAPI().createNewPackage();

		this.assertAreSimilar(this.toList(obj1), this.toList(obj2), Boolean.FALSE);
	}
}
