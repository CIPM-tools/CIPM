/**
 * Contains dummy implementations of initialiser packages as well as their
 * content. They are used in tests for initialiser packages and initialiser
 * adaptation. Through these tests, the recursive discovery of sub-packages and
 * their content is tested. The current package/initialiser layout is as
 * follows: <br>
 * <br>
 * <ul>
 * <li>{@link DummyTopLevelInitialiserPackage}
 * <ul>
 * <li>{@link DummyAggregateInitialiserPackageOne}
 * <ul>
 * <li>{@link DummyInitialiserE}
 * <li>{@link DummyInitialiserPackageA}
 * <ul>
 * <li>{@link DummyInitialiserA}
 * </ul>
 * <li>{@link DummyInitialiserPackageB}
 * <ul>
 * <li>{@link DummyInitialiserB}
 * </ul>
 * </ul>
 * <li>{@link DummyAggregateInitialiserPackageTwo}
 * <ul>
 * <li>{@link DummyInitialiserPackageCD}
 * <ul>
 * <li>{@link DummyInitialiserC}
 * <li>{@link DummyInitialiserD}
 * </ul>
 * </ul>
 * </ul>
 * </ul>
 * 
 * @author Alp Torac Genc
 */
package cipm.consistency.initialisers.tests.dummy.packages;