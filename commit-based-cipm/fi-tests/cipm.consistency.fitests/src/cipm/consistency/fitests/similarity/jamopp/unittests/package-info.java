/**
 * Contains interfaces that can be implemented by tests, where initialisers are
 * used to instantiate {@link EObject} implementors present in JaMoPP. Each
 * initialiser in this package offers default methods that help encapsulate the
 * creation of such EObject instances, which are needed in tests. <br>
 * <br>
 * It is recommended to only use the interfaces in this package to instantiate
 * EObject instances that are needed to setup the "main" EObject instances under
 * test (i.e. instances used in similarity checking methods). The creation of
 * the "main" EObject instances should be done within the corresponding test
 * class explicitly via initialisers, in order to avoid implicit steps as much
 * as possible. <br>
 * <br>
 * While adding further interfaces to this package, it is suggested to check the
 * existing interfaces and to extend them, if possible. Doing so will help keep
 * consistency across test cases. <br>
 * <br>
 * The sub-packages of this package contain unit tests for EObject
 * classes/interfaces using initialisers. For each (changeable,
 * {@code eObj.isChangeable()}) attribute/feature X of EObject sub-type EO,
 * there are currently up to 3 test categories. Let eo1 and eo2 be instances of
 * type EO, then test categories do the following:
 * <ul>
 * <li><b>testX</b>: Performs similarity checking on eo1 and eo2, whose
 * attributes are all equal except for X, and checks the result. For
 * <li><b>testXSize</b>: Same as testX, where X is a many-values attribute (i.e.
 * {@code X.isMany()}, meaning that X's value is an array) and its values in eo1
 * and eo2 contain different amounts of elements: X of one eo contains 2
 * elements elem1 and elem2 (elem1 and elem2 are NOT similar), whereas the X of
 * other eo only contains elem1: {@code eo1.eGet(X) = [elem1, elem2];
 * eo2.eGet(X) = [elem1]}. Notice that elem1 is mutual in both and is the first
 * element in both cases. This helps ensure that:
 * <ol>
 * <li>Array lengths are accounted for,
 * <li>Elements are being compared in the correct order (i.e. elem1 with elem1
 * and not elem2 with elem1).
 * </ol>
 * <li><b>testXNullCheck</b>: Same as testX, where eo1's X attribute is set and
 * eo2's X attribute is not. These test methods ensure that no exceptions are
 * thrown while performing similarity checking on EObject instances that are not
 * initialised properly.
 * </ul>
 * <b>Note that multiple similarity checks are performed in each test, in order
 * to ensure that similarity checking is symmetric and that similarity checking
 * yields the same result, if the EObject instances are cloned. Refer to the
 * documentation of the similarity checking methods concrete test methods use
 * for more information.</b>
 */
package cipm.consistency.fitests.similarity.jamopp.unittests;